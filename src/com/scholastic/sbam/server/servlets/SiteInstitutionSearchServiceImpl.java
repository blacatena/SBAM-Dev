package com.scholastic.sbam.server.servlets;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.scholastic.sbam.client.services.SiteInstitutionSearchService;
import com.scholastic.sbam.server.fastSearch.SiteInstitutionCache;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.server.fastSearch.InstitutionCache.InstitutionCacheConflict;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class SiteInstitutionSearchServiceImpl extends InstitutionSearchServiceImpl implements SiteInstitutionSearchService {

	@Override
	public SynchronizedPagingLoadResult<InstitutionInstance> getSiteInstitutions(PagingLoadConfig loadConfig, String filter, boolean includeAgreementSummaries, long syncId) throws IllegalArgumentException, ServiceNotReadyException {
		authenticate("search site institutions", SecurityManager.ROLE_QUERY);
		return doSearch(loadConfig, filter, includeAgreementSummaries, syncId);
	}
	
	protected InstitutionCache getSearchCache() throws InstitutionCacheConflict {
		if (SiteInstitutionCache.getSingleton() == null || !SiteInstitutionCache.getSingleton().isMapsReady())
			throw new ServiceNotReadyException("site institution search function");
		return SiteInstitutionCache.getSingleton();
	}
}
