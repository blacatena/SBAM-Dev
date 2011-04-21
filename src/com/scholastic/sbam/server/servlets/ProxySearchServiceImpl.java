package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.scholastic.sbam.client.services.ProxySearchService;
import com.scholastic.sbam.server.database.codegen.Proxy;
import com.scholastic.sbam.server.database.objects.DbProxy;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.ProxyInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;
import com.scholastic.sbam.shared.util.SearchUtilities;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ProxySearchServiceImpl extends AuthenticatedServiceServlet implements ProxySearchService {

	@Override
	public SynchronizedPagingLoadResult<ProxyInstance> searchProxies(PagingLoadConfig loadConfig, String filter, long syncId) throws IllegalArgumentException, ServiceNotReadyException {
		
		authenticate("search proxies", SecurityManager.ROLE_QUERY);

		// This is just a BasePagingLoadResult,but it has a synchronization tag to make sure old, slow, late search results don't overwrite newer, better results
		SynchronizedPagingLoadResult<ProxyInstance> result = null;
		List<ProxyInstance> list = new ArrayList<ProxyInstance>();
		
		if (filter == null || filter.trim().length() == 0) {
			return new SynchronizedPagingLoadResult<ProxyInstance>(list, loadConfig.getOffset(), 0, syncId);
		}
	
		try {
			
			//	Determine search type -- loose boolean, boolean, or natural language
			SearchUtilities.SearchSettings searchSettings = new SearchUtilities.SearchSettings(filter);
			filter = searchSettings.getFilter();
				
			HibernateUtil.openSession();
			HibernateUtil.startTransaction();
			
			String [] filters = AppConstants.parseFilterTerms(filter);

			List<Proxy> dbInstances = DbProxy.findFiltered(filter, searchSettings.isDoBoolean(), AppConstants.STATUS_ACTIVE, AppConstants.STATUS_ANY_NONE, loadConfig.getSortField(), loadConfig.getSortDir());
			
			int i = 0;
			int totSize = 0;

			for (Proxy proxy : dbInstances) {
				if ((searchSettings.isDoBoolean() && searchSettings.isDoLoose()) || qualifyProxy(proxy, filters)) {
					totSize++;
					//	Paging... start from where asked, and don't return more than requested
					if (i >= loadConfig.getOffset() && (list.size() < loadConfig.getLimit() || loadConfig.getLimit() <= 0)) {
						ProxyInstance proxyInstance	= DbProxy.getInstance(proxy);
						list.add(proxyInstance);
					}
					i++;
				}
			}
			
			result = new SynchronizedPagingLoadResult<ProxyInstance>(list, loadConfig.getOffset(), totSize, syncId);
			
			HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
			

		} catch (Exception exc) {
			exc.printStackTrace();
			throw new IllegalArgumentException("Unexpected failure on proxy search: " + exc.getMessage());
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
	private boolean qualifyProxy(Proxy proxy, String [] filters) {
		for (int i = 0; i < filters.length; i++) {
			String filter = filters [i].trim().toUpperCase();
			if (filter.length() == 0)
				continue;
			
			if ( proxy.getDescription().toUpperCase().indexOf(filter) < 0
			&&   proxy.getSearchKeys().toUpperCase().indexOf(filter) < 0 )
				return false;
		}
		return true;
	}
}
