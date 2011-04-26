package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.scholastic.sbam.client.services.AgreementSiteInstitutionSearchService;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.objects.DbAgreementSite;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.server.fastSearch.InstitutionCache.InstitutionCacheConflict;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AgreementSiteInstitutionSearchServiceImpl extends AuthenticatedServiceServlet implements AgreementSiteInstitutionSearchService {
	
	@Override
	public SynchronizedPagingLoadResult<InstitutionInstance> searchAgreementSiteInstitutions(PagingLoadConfig loadConfig, int agreementId, String filter, char neStatus, long syncId) throws IllegalArgumentException {
		
		authenticate("search site locations", SecurityManager.ROLE_QUERY);

		// This is just a BasePagingLoadResult,but it has a synchronization tag to make sure old, slow, late search results don't overwrite newer, better results
		SynchronizedPagingLoadResult<InstitutionInstance> result = null;
		List<InstitutionInstance> list = new ArrayList<InstitutionInstance>();
		
		if (agreementId <= 0) {	// && (filter == null || filter.trim().length() == 0)) {
			return new SynchronizedPagingLoadResult<InstitutionInstance>(list, loadConfig.getOffset(), 0, syncId);
		}
	
		try {
				
			HibernateUtil.openSession();
			HibernateUtil.startTransaction();
			
			
			//	MySQL match is acting buggy (fails on some searches)
//			SearchUtilities.SearchSettings searchSettings = new SearchUtilities.SearchSettings(filter);
//			List<Institution> institutions = DbAgreementSite.findInstitutions(agreementId, searchSettings.getFilter(), searchSettings.isDoBoolean(), neStatus);
			
			//	So just get them all, and filter them with Java
			List<Institution> institutions = DbAgreementSite.findInstitutions(agreementId, null, true, neStatus);
			
			int i = 0;
			int totSize = 0;

			String [] filters = AppConstants.parseFilterTerms(filter);
			
			for (Institution institution : institutions) {
				if (!qualifyInstitution(institution, filters))
					continue;
				totSize++;
				//	Paging... start from where asked, and don't return more than requested
				if (i >= loadConfig.getOffset() && (list.size() < loadConfig.getLimit() || loadConfig.getLimit() <= 0)) {
					InstitutionInstance institutionInstance	= DbInstitution.getInstance(institution);
					setDescriptions(institutionInstance);
					list.add(institutionInstance);
				}
				i++;
			}
			
			result = new SynchronizedPagingLoadResult<InstitutionInstance>(list, loadConfig.getOffset(), totSize, syncId);
			
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
	 * Apply all filters to be sure this address qualifies.
	 * @param dbInstance
	 * @param filters
	 * @return
	 */
	private boolean qualifyInstitution(Institution dbInstance, String [] filters) {
		for (int i = 0; i < filters.length; i++) {
			String filter = filters [i].trim().toUpperCase();
			if (filter.length() == 0)
				continue;
			if (dbInstance.getInstitutionName().toUpperCase().indexOf(filter) < 0
			&&  dbInstance.getAddress1().toUpperCase().indexOf(filter) < 0
			&&  dbInstance.getCity().toUpperCase().indexOf(filter) < 0
			&&  dbInstance.getState().toUpperCase().indexOf(filter) < 0
			&&  dbInstance.getZip().toUpperCase().indexOf(filter) < 0
			&&  dbInstance.getAddress2().toUpperCase().indexOf(filter) < 0
			&&  dbInstance.getAddress3().toUpperCase().indexOf(filter) < 0
			&&  dbInstance.getCountry().toUpperCase().indexOf(filter) < 0
//			&&  (dbInstance.getPhone() == null || dbInstance.getPhone().toUpperCase().indexOf(filter) < 0)
//			&&  (dbInstance.getFax() == null || dbInstance.getFax().toUpperCase().indexOf(filter) < 0)
			&&  (dbInstance.getUcn() + "").indexOf(filter) < 0
			&&  (dbInstance.getParentUcn() + "").indexOf(filter) < 0
			&&  dbInstance.getAlternateIds().toUpperCase().indexOf(filter) < 0)
				return false;
		}
		return true;
	}
	
	private void setDescriptions(InstitutionInstance institutionInstance) throws InstitutionCacheConflict {
		if (institutionInstance == null)
			return;
		
		InstitutionCache.getSingleton().setDescriptions( institutionInstance );
	}
}
