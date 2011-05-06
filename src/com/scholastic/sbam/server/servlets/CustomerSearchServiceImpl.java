package com.scholastic.sbam.server.servlets;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.scholastic.sbam.client.services.CustomerSearchService;
import com.scholastic.sbam.server.fastSearch.CustomerCache;
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
public class CustomerSearchServiceImpl extends InstitutionSearchServiceImpl implements CustomerSearchService {

	@Override
	public SynchronizedPagingLoadResult<InstitutionInstance> getCustomers(PagingLoadConfig loadConfig, String filter, boolean includeAgreementSummaries, long syncId) throws IllegalArgumentException, ServiceNotReadyException {
		authenticate("search institutions", SecurityManager.ROLE_QUERY);
		return doSearch(loadConfig, filter, includeAgreementSummaries, syncId);
	}
	
	protected InstitutionCache getSearchCache() throws InstitutionCacheConflict {
		if (CustomerCache.getSingleton() == null || !CustomerCache.getSingleton().isMapsReady())
			throw new ServiceNotReadyException("customer search function");
		return CustomerCache.getSingleton();
	}
}
