package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.scholastic.sbam.client.services.UserCacheListService;
import com.scholastic.sbam.server.database.codegen.UserCache;
import com.scholastic.sbam.server.database.objects.DbUserCache;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.objects.UserCacheInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UserCacheListServiceImpl extends AuthenticatedServiceServlet implements UserCacheListService {

	@Override
	public SynchronizedPagingLoadResult<UserCacheInstance> getUserCacheTargets(PagingLoadConfig loadConfig, String userName, String category, Date fromDate, int maxCount, long syncId) throws IllegalArgumentException {
		
		Authentication auth = authenticate("list user cache",	SecurityManager.ROLE_QUERY);
		
		if (userName == null)
			userName = auth.getUserName();
		
		//	Need another level of security to look at someone else's cache
		if (!auth.getUserName().equals(userName))
			auth = authenticate("list foreign user cache", SecurityManager.ROLE_ADMIN);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		SynchronizedPagingLoadResult<UserCacheInstance> result = null;
		List<UserCacheInstance> list = new ArrayList<UserCacheInstance>();
		try {
			
			List<UserCache> dbInstances;
			dbInstances = DbUserCache.findByUserName(userName, category, fromDate);
			
			int i = 0;

			for (UserCache dbInstance : dbInstances) {

				//	Paging... start from where asked, and don't return more than requested
				if (i >= loadConfig.getOffset() && list.size() < loadConfig.getLimit()) {
					list.add(DbUserCache.getInstance(dbInstance));			
				}
				i++;
				
				if (list.size() > loadConfig.getLimit())
					break;
				
				// If we have a limit, stop when reached
				if (maxCount > 0 && list.size() >= maxCount)
					break;
			}

			result = new SynchronizedPagingLoadResult<UserCacheInstance>(list, loadConfig.getOffset(), dbInstances.size(), syncId);

		} catch (Exception exc) {
			exc.printStackTrace();
			result = new SynchronizedPagingLoadResult<UserCacheInstance>(list, loadConfig.getOffset(), 0, syncId);
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return result;
	}
}
