package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.CacheRefreshService;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.CustomerCache;
import com.scholastic.sbam.server.fastSearch.HelpTextCache;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.server.fastSearch.SiteInstitutionCache;
import com.scholastic.sbam.server.fastSearch.InstitutionCache.InstitutionCacheConflict;
import com.scholastic.sbam.shared.objects.CacheStatusInstance;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class CacheRefreshServiceImpl extends AuthenticatedServiceServlet implements CacheRefreshService {

	@Override
	public CacheStatusInstance refreshCache(String cacheKey) throws IllegalArgumentException {
		
		authenticate("list link types");	//	SecurityManager.ROLE_CONFIG);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		CacheStatusInstance cache = null;
		try {
			
			cache = doCacheRefresh(cacheKey);

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return cache;
	}
	
	public CacheStatusInstance doCacheRefresh(String key) throws InstitutionCacheConflict, Exception {
		if (CacheStatusInstance.CUSTOMER_CACHE_KEY.equals(key)) {
			
			if (CustomerCache.getSingleton().isMapsReady())
				CustomerCache.getSingleton().refresh();
			else
				throw new IllegalArgumentException("Customer cache is currently initializing.");
				
			return CustomerCache.getSingleton().getCacheStatus();
			
		} else if (CacheStatusInstance.HELP_TEXT_CACHE_KEY.equals(key)) {
			
			if (HelpTextCache.getSingleton().isMapsReady())
				HelpTextCache.getSingleton().refresh();
			else
				throw new IllegalArgumentException("Help text cache is currently initializing.");
				
			return HelpTextCache.getSingleton().getCacheStatus();
			
		} else if (CacheStatusInstance.INSTITUTION_CACHE_KEY.equals(key)) {
			
			if (InstitutionCache.getSingleton().isMapsReady())
				InstitutionCache.getSingleton().refresh();
			else
				throw new IllegalArgumentException("Institution cache is currently initializing.");
				
			return InstitutionCache.getSingleton().getCacheStatus();
			
		} else if (CacheStatusInstance.SITE_CACHE_KEY.equals(key)) {
			
			if (SiteInstitutionCache.getSingleton().isMapsReady())
				SiteInstitutionCache.getSingleton().refresh();
			else
				throw new IllegalArgumentException("Site Institution cache is currently initializing.");
				
			return SiteInstitutionCache.getSingleton().getCacheStatus();
			
		} else
			throw new IllegalArgumentException("Invalid cache key value " + key + ".");
	}
}
