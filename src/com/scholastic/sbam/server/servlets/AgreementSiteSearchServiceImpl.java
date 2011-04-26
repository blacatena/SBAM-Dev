package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.scholastic.sbam.client.services.AgreementSiteSearchService;
import com.scholastic.sbam.server.database.codegen.AgreementSite;
import com.scholastic.sbam.server.database.objects.DbAgreementSite;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.server.fastSearch.InstitutionCache.InstitutionCacheConflict;
import com.scholastic.sbam.shared.objects.AgreementSiteInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.SearchUtilities;

/**
 * The server side implementation of the RPC service.
 * 
 * INCOMPLETE -- TEST AND FINISH BEFORE USING
 * 
 * This class was created to use in conjunction with the AgreementSiteSearchField for finding the sites (ucn+suffix+location) for an agreement.
 * 
 * It was abandoned in favor of a two field approach (institution, location).
 * 
 * This class could be optimized by changing DbAgreementSite to return both the AgreementSite and Institution entities, so that they could be merged without further database access (i.e
 * create the AgreementSiteInstance and InstitutionInstance, then populate the former with the latter. 
 * 
 * To be used this must be expanded and properly tested.
 */
@SuppressWarnings("serial")
public class AgreementSiteSearchServiceImpl extends AuthenticatedServiceServlet implements AgreementSiteSearchService {
	
	@Override
	public SynchronizedPagingLoadResult<AgreementSiteInstance> searchAgreementSites(PagingLoadConfig loadConfig, int agreementId, String filter, char neStatus, long syncId) throws IllegalArgumentException {
		
		authenticate("search site locations", SecurityManager.ROLE_QUERY);

		// This is just a BasePagingLoadResult,but it has a synchronization tag to make sure old, slow, late search results don't overwrite newer, better results
		SynchronizedPagingLoadResult<AgreementSiteInstance> result = null;
		List<AgreementSiteInstance> list = new ArrayList<AgreementSiteInstance>();
		
		if (agreementId <= 0) {	// && (filter == null || filter.trim().length() == 0)) {
			return new SynchronizedPagingLoadResult<AgreementSiteInstance>(list, loadConfig.getOffset(), 0, syncId);
		}
	
		try {
				
			HibernateUtil.openSession();
			HibernateUtil.startTransaction();
			
			SearchUtilities.SearchSettings searchSettings = new SearchUtilities.SearchSettings(filter);
			
			//	Find only undeleted site types
			List<AgreementSite> siteInstances = DbAgreementSite.findFiltered(agreementId, searchSettings.getFilter(), searchSettings.isDoBoolean(), neStatus);
			
			int i = 0;
			int totSize = 0;

			for (AgreementSite site : siteInstances) {
				totSize++;
				//	Paging... start from where asked, and don't return more than requested
				if (i >= loadConfig.getOffset() && (list.size() < loadConfig.getLimit() || loadConfig.getLimit() <= 0)) {
					AgreementSiteInstance siteInstance	= DbAgreementSite.getInstance(site);
					setDescriptions(siteInstance);
					list.add(siteInstance);
				}
				i++;
			}
			
			result = new SynchronizedPagingLoadResult<AgreementSiteInstance>(list, loadConfig.getOffset(), totSize, syncId);
			
			HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
			

		} catch (Exception exc) {
			exc.printStackTrace();
			throw new IllegalArgumentException("Unexpected failure on site location search: " + exc.getMessage());
		} finally {
			
		}
		
		return result;
	}
	
	private void setDescriptions(AgreementSiteInstance agreementSite) throws InstitutionCacheConflict {
		if (agreementSite == null)
			return;
		
		DbAgreementSite.setDescriptions(agreementSite);
		if (agreementSite.getSite() != null) {
			InstitutionCache.getSingleton().setDescriptions( agreementSite.getSite().getInstitution() );
		}
	}
}
