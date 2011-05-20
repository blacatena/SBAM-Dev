package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.FilterConfig;
import com.extjs.gxt.ui.client.data.FilterPagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.scholastic.sbam.client.services.AgreementSiteListService;
import com.scholastic.sbam.server.database.codegen.AgreementSite;
import com.scholastic.sbam.server.database.objects.DbAgreementSite;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.server.fastSearch.InstitutionCache.InstitutionCacheConflict;
import com.scholastic.sbam.shared.objects.AgreementSiteInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AgreementSiteListServiceImpl extends AuthenticatedServiceServlet implements AgreementSiteListService {
	
	private class siteComparator<AgreemmentSiteInstance> implements Comparator<AgreemmentSiteInstance> {
		private String	sortField;
		private int     reverse;
		
		public siteComparator(String sortField, SortDir sortDir) {
			this.sortField = sortField;
			if (sortDir == SortDir.ASC)
				reverse = 1;
			else
				reverse = -1;
		}

		@Override
		public int compare(Object o1, Object o2) {
			int result = compare(o1, o2, sortField);
			if (result != 0)
				return result;
			result = compare(o1, o2, "siteUcn");
			if (result != 0)
				return result;
			result = compare(o1, o2, "siteUcnSuffix");
			if (result != 0)
				return result;
			result = compare(o1, o2, "siteLocCode");
			return result;
		}
		
		public int compare(Object o1, Object o2, String sortField) {
			if (sortField == null || sortField.length() == 0)
				return 0;
			
			AgreementSiteInstance a1 = (AgreementSiteInstance) o1;
			AgreementSiteInstance a2 = (AgreementSiteInstance) o2;
			
			if (sortField.equals("displayUcn")) {
				return reverse * a1.getDisplayUcn().compareTo(a2.getDisplayUcn());
			}
			if (sortField.equals("siteUcn")) {
				return reverse * new Integer(a1.getSiteUcn()).compareTo(a2.getSiteUcn());
			}
			if (sortField.equals("siteUcnSuffix")) {
				return reverse * new Integer(a1.getSiteUcnSuffix()).compareTo(a2.getSiteUcnSuffix());
			}
			if (sortField.equals("siteLocCode")) {
				return reverse * a1.getSiteLocCode().compareTo(a2.getSiteLocCode());
			}
			if (sortField.equals("statusDescription")) {
				return reverse * a1.getStatusDescription().compareTo(a2.getStatusDescription());
			}
			if (sortField.equals("site.description")) {
				return reverse * a1.getSite().getDescription().compareTo(a2.getSite().getDescription());
			}
			if (sortField.equals("site.institution.institutionName")) {
				if (a1.getSite() == null)
					return reverse * a1.getDisplayUcn().compareTo(a2.getDisplayUcn());;
				return reverse * a1.getSite().getInstitution().getInstitutionName().compareTo(a2.getSite().getInstitution().getInstitutionName());
			}
			if (sortField.equals("site.institution.htmlAddress")) {
				if (a1.getSite() == null)
					return reverse * a1.getDisplayUcn().compareTo(a2.getDisplayUcn());;
				if (a1.getSite().getInstitution() == null)
					return reverse * a1.getDisplayUcn().compareTo(a2.getDisplayUcn());;
				int r = reverse * a1.getSite().getInstitution().getAddress1().compareTo(a2.getSite().getInstitution().getAddress1());
				if (r != 0)
					return r;
				if (a1.getSite().getInstitution().getAddress2() != null)
					r = reverse * a1.getSite().getInstitution().getAddress2().compareTo(a2.getSite().getInstitution().getAddress2());
				if (r != 0)
					return r;
				if (a1.getSite().getInstitution().getAddress3() != null)
					r = reverse * a1.getSite().getInstitution().getAddress3().compareTo(a2.getSite().getInstitution().getAddress3());
				if (r != 0)
					return r;
				if (a1.getSite().getInstitution().getCity() != null)
					r = reverse * a1.getSite().getInstitution().getCity().compareTo(a2.getSite().getInstitution().getCity());
				if (r != 0)
					return r;
				if (a1.getSite().getInstitution().getState() != null)
					r = reverse * a1.getSite().getInstitution().getState().compareTo(a2.getSite().getInstitution().getState());
				if (r != 0)
					return r;
				if (a1.getSite().getInstitution().getZip() != null)
					r = reverse * a1.getSite().getInstitution().getZip().compareTo(a2.getSite().getInstitution().getZip());
				if (r != 0)
					return r;
				if (a1.getSite().getInstitution().getCountry() != null)
					r = reverse * a1.getSite().getInstitution().getCountry().compareTo(a2.getSite().getInstitution().getCountry());
			}
				
			return reverse * a1.getDisplayUcn().compareTo(a2.getDisplayUcn());
		}
		
	}

	@Override
	public SynchronizedPagingLoadResult<AgreementSiteInstance> getAgreementSites(PagingLoadConfig loadConfig, int agreementId, char neStatus, long syncId) throws IllegalArgumentException {
		
		authenticate("get agreement sites", SecurityManager.ROLE_QUERY);
		
		if (agreementId == 0)
			return new SynchronizedPagingLoadResult<AgreementSiteInstance>( new ArrayList<AgreementSiteInstance>(), loadConfig.getOffset(), 0, syncId);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		int totSize = 0;
		List<AgreementSiteInstance> list = new ArrayList<AgreementSiteInstance>();
		try {
			//	Find only undeleted site types
			List<AgreementSite> sites = DbAgreementSite.findByAgreementId(agreementId, AppConstants.STATUS_ANY_NONE, neStatus);
			
			//	Build a list first... we only do this in case we need to sort it in the next step... which we may not
			List<AgreementSiteInstance> siteInstances = new ArrayList<AgreementSiteInstance>();
			for (AgreementSite site : sites) {
				siteInstances.add(DbAgreementSite.getInstance(site));
			}
			
			//	Resort if asked
			if (loadConfig != null && loadConfig.getSortField() != null && loadConfig.getSortField().length() > 0) {
				//	If the sort field contains a '.' then we're going to need the associated descriptions
				if (loadConfig.getSortField().indexOf('.') >= 0)
					for (AgreementSiteInstance siteInstance : siteInstances)
						setDescriptions(siteInstance);
				Collections.sort(siteInstances, new siteComparator<AgreementSiteInstance>(loadConfig.getSortField(), loadConfig.getSortDir()));
			}
			
			int i = 0;
			
			for (AgreementSiteInstance siteInstance : siteInstances) {
				
				if (!qualifyFilters(siteInstance, loadConfig))
					continue;
				
				totSize++;
				//	Paging... start from where asked, and don't return more than requested -- if no load config, return everything
				if (loadConfig == null || (i >= loadConfig.getOffset() && list.size() < loadConfig.getLimit())) {
					if (siteInstance.getSite() == null)	//	If we haven't added the descriptions by now, do so
						setDescriptions(siteInstance);	//  This optimization means that in the simplest case, where descriptions are not needed
														//  for filters or sorts, we only bother to do it for those results that qualify
					list.add(siteInstance);
				}
				i++;
				
			}
			
			for (AgreementSiteInstance site : list) {
				setDescriptions(site);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return new SynchronizedPagingLoadResult<AgreementSiteInstance>(list, loadConfig.getOffset(), totSize, syncId);
	}
	
	public boolean qualifyFilters(AgreementSiteInstance siteInstance, PagingLoadConfig loadConfig) throws InstitutionCacheConflict {
		if (! (loadConfig instanceof FilterPagingLoadConfig) )
			return true;
		
		List<FilterConfig> filters = ( (FilterPagingLoadConfig) loadConfig ).getFilterConfigs();
		if (filters == null || filters.size() == 0)
			return true;
		
		if (siteInstance.getSite() == null)	// optimization, in case this has already been set
			setDescriptions(siteInstance);
		
		for (FilterConfig filter : filters) {
			
			if ("displayUcn".equals(filter.getField()) && filterFails(filter, siteInstance.getDisplayUcn()))
				return false;
			
			if ("siteUcn".equals(filter.getField()) && filterFails(filter, siteInstance.getSiteUcn()))
				return false;
			
			if ("siteUcnSuffix".equals(filter.getField()) && filterFails(filter, siteInstance.getSiteUcnSuffix()))
				return false;
			
			if ("siteLocCode".equals(filter.getField()) && filterFails(filter, siteInstance.getSiteLocCode()))
				return false;

			if ("statusDescription".equals(filter.getField()) && filterFails(filter, siteInstance.getStatusDescription()))
				return false;
			
			if ("site.description".equals(filter.getField()) && filterFails(filter, siteInstance.getSite().getDescription()))
				return false;
			
			if ("site.institution.institutionName".equals(filter.getField()) && filterFails(filter, siteInstance.getSite().getInstitution().getInstitutionName()))
				return false;
			
			if ("site.institution.htmlAddress".equals(filter.getField()) 
			&&  filterFails(filter, siteInstance.getSite().getInstitution().getAddress1())
			&&  filterFails(filter, siteInstance.getSite().getInstitution().getAddress2())
			&&  filterFails(filter, siteInstance.getSite().getInstitution().getAddress3())
			&&  filterFails(filter, siteInstance.getSite().getInstitution().getCity())
			&&  filterFails(filter, siteInstance.getSite().getInstitution().getState())
			&&  filterFails(filter, siteInstance.getSite().getInstitution().getZip())
			&&  filterFails(filter, siteInstance.getSite().getInstitution().getCountry()))
				return false;

		}
		return true;
	}

	public boolean filterFails(FilterConfig filter, String value) {
		if (filter.getValue() != null && filter.getValue().toString().length() > 0)
			if (value == null || value.toLowerCase().indexOf(filter.getValue().toString().toLowerCase()) < 0)
				return true;
		return false;
	}

	public boolean filterFails(FilterConfig filter, int value) {
		if (filter.getValue() != null && filter.getValue() instanceof Double) {
			int filterValue = ((Double) filter.getValue()).intValue();
			if ("lt".equals(filter.getComparison()))
				return (value >= filterValue);
			if ("gt".equals(filter.getComparison()))
				return (value <= filterValue);
			return (value != filterValue);
		}
		return false;
	}

	public boolean filterFails(FilterConfig filter, double value) {
		if (filter.getValue() != null && filter.getValue() instanceof Double) {
			double filterValue = ((Double) filter.getValue()).doubleValue();
			if ("lt".equals(filter.getComparison()))
				return (value < filterValue);
			if ("gt".equals(filter.getComparison()))
				return (value > filterValue);
			return (value == filterValue);
		}
		return false;
	}
	
	private void setDescriptions(AgreementSiteInstance agreementSite) throws InstitutionCacheConflict {
		if (agreementSite == null)
			return;
		
		DbAgreementSite.setDescriptions(agreementSite);
		if (agreementSite.getSite() != null)
			InstitutionCache.getSingleton().setDescriptions( agreementSite.getSite().getInstitution() );
	}
}
