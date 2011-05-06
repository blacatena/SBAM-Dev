package com.scholastic.sbam.server.servlets;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.scholastic.sbam.client.services.CustomerWordService;
import com.scholastic.sbam.server.fastSearch.CustomerCache;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.server.fastSearch.InstitutionCache.InstitutionCacheConflict;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.FilterWordInstance;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class CustomerWordServiceImpl extends InstitutionWordServiceImpl implements CustomerWordService {

	@Override
	public PagingLoadResult<FilterWordInstance> getCustomerWords(PagingLoadConfig loadConfig) throws IllegalArgumentException, ServiceNotReadyException {		
		authenticate(getSubjectName() + " word list");	//	SecurityManager.ROLE_CONFIG);
		return doWordSearch(loadConfig);
	}
	
	public String getSubjectName() {
		return "customer";
	}
	
	public InstitutionCache getSearchCache() throws InstitutionCacheConflict {
		return CustomerCache.getSingleton();
	}
}
