package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.scholastic.sbam.client.services.SiteLocationSearchService;
import com.scholastic.sbam.server.database.codegen.AgreementSite;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.objects.DbAgreementSite;
import com.scholastic.sbam.server.database.objects.DbSite;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class SiteLocationSearchServiceImpl extends AuthenticatedServiceServlet implements SiteLocationSearchService {

	@Override
	public SynchronizedPagingLoadResult<SiteInstance> searchSiteLocations(PagingLoadConfig loadConfig, int agreementId, int ucn, int ucnSuffix, String filter, long syncId) throws IllegalArgumentException, ServiceNotReadyException {
		
		authenticate("search site locations", SecurityManager.ROLE_QUERY);

		// This is just a BasePagingLoadResult,but it has a synchronization tag to make sure old, slow, late search results don't overwrite newer, better results
		SynchronizedPagingLoadResult<SiteInstance> result = null;
		List<SiteInstance> list = new ArrayList<SiteInstance>();
		
		if (ucn <= 0) {	// && (filter == null || filter.trim().length() == 0)) {
			return new SynchronizedPagingLoadResult<SiteInstance>(list, loadConfig.getOffset(), 0, syncId);
		}
	
		try {
			HibernateUtil.openSession();
			HibernateUtil.startTransaction();
			
			String [] filters = AppConstants.parseFilterTerms(filter);
			
			//	Get all sites for the institution
			List<Site> dbInstances = DbSite.findByUcn(ucn, ucnSuffix, AppConstants.STATUS_ACTIVE, AppConstants.STATUS_ANY_NONE);	//, loadConfig.getSortField(), loadConfig.getSortDir());
			
			//	If an agreementId was supplied, filter by agreement ID
			dbInstances = filterByAgreement(agreementId, dbInstances);
			
			int i = 0;
			int totSize = 0;

			for (Site site : dbInstances) {
				if (qualifySite(site, filters)) {
					totSize++;
					//	Paging... start from where asked, and don't return more than requested
					if (i >= loadConfig.getOffset() && (list.size() < loadConfig.getLimit() || loadConfig.getLimit() <= 0)) {
						SiteInstance siteInstance	= DbSite.getInstance(site);
						DbSite.setDescriptions(siteInstance);
						list.add(siteInstance);
					}
					i++;
				}
			}
			
			result = new SynchronizedPagingLoadResult<SiteInstance>(list, loadConfig.getOffset(), totSize, syncId);
			
			HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
			

		} catch (Exception exc) {
			exc.printStackTrace();
			throw new IllegalArgumentException("Unexpected failure on site location search: " + exc.getMessage());
		} finally {
			
		}
		
		return result;
	}
	
	/**
	 * If an agreement ID is specified, restrict 
	 * @param agreementId
	 * @param sites
	 * @return
	 */
	protected List<Site> filterByAgreement(int agreementId, List<Site> sites) {
		//	If no agreement ID is specified, just return all of the sites
		if (agreementId <= 0)
			return sites;
		
		//	If an agreement ID is included, restrict to those included on the agreement
		List<AgreementSite> agreementSites = DbAgreementSite.findByAgreementId(agreementId, AppConstants.STATUS_ACTIVE, AppConstants.STATUS_ANY_NONE);
		
		//	First pass... look for an "all sites" entry, and if found, everything is valid, otherwise, add to a hash set
		HashSet<String> validCodes = new HashSet<String>();
		for (AgreementSite agreementSite : agreementSites) {
			if ("".equals(agreementSite.getId().getSiteLocCode()))
				return sites;
			else
				validCodes.add(agreementSite.getId().getSiteLocCode());
		}
		
		//	Second pass... only include those sites that are explicitly listed on the agreement.
		List<Site> newSites = new ArrayList<Site>();
		for (Site site : sites) {
			if (validCodes.contains(site.getId().getSiteLocCode()))
				newSites.add(site);
		}
		
		return newSites;
	}
	
	/**
	 * Apply all filters to be sure this address qualifies.
	 * @param dbInstance
	 * @param filters
	 * @return
	 */
	private boolean qualifySite(Site site, String [] filters) {
		for (int i = 0; i < filters.length; i++) {
			String filter = filters [i].trim().toUpperCase();
			if (filter.length() == 0)
				continue;
			
			
			if ( site.getId().getSiteLocCode().toUpperCase().indexOf(filter) < 0
			&&  site.getDescription().toUpperCase().indexOf(filter) < 0 )
				return false;
		}
		return true;
	}
}
