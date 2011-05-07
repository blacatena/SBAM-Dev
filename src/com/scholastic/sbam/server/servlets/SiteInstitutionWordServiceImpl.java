package com.scholastic.sbam.server.servlets;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.scholastic.sbam.client.services.SiteInstitutionWordService;
import com.scholastic.sbam.server.fastSearch.SiteInstitutionCache;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.server.fastSearch.InstitutionCache.InstitutionCacheConflict;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.FilterWordInstance;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class SiteInstitutionWordServiceImpl extends InstitutionWordServiceImpl implements SiteInstitutionWordService {

	@Override
	public PagingLoadResult<FilterWordInstance> getSiteInstitutionWords(PagingLoadConfig loadConfig) throws IllegalArgumentException, ServiceNotReadyException {		
		authenticate(getSubjectName() + " word list");	//	SecurityManager.ROLE_CONFIG);
		return doWordSearch(loadConfig);
	}
	
	public String getSubjectName() {
		return "site institution";
	}
	
	public InstitutionCache getSearchCache() throws InstitutionCacheConflict {
		return SiteInstitutionCache.getSingleton();
	}
}
