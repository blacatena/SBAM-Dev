package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.scholastic.sbam.client.services.UserCacheListService;
import com.scholastic.sbam.server.database.codegen.UserCache;
import com.scholastic.sbam.server.database.objects.DbUserCache;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.objects.UserCacheInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UserCacheListServiceImpl extends AuthenticatedServiceServlet implements UserCacheListService {

	@Override
	public List<UserCacheInstance> getUserCacheTargets(LoadConfig loadConfig, String userName, String category, Date fromDate, int maxCount, boolean restoreOnly) throws IllegalArgumentException {
		
		Authentication auth = authenticate("list user cache",	SecurityManager.ROLE_QUERY);
		
		if (userName == null)
			userName = auth.getUserName();
		
		//	Need another level of security to look at someone else's cache
		if (!auth.getUserName().equals(userName))
			auth = authenticate("list foreign user cache", SecurityManager.ROLE_ADMIN);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<UserCacheInstance> list = new ArrayList<UserCacheInstance>();
		try {
			
			List<UserCache> dbInstances;
			if (restoreOnly)
				dbInstances = DbUserCache.findByUserName(userName, category, fromDate);
			else
				dbInstances = DbUserCache.findToRestore(userName);

			UserCache prevInstance = null;
			for (UserCache dbInstance : dbInstances) {
				// If doing a restore, load only the most recent access at a particular portlet position (in case one active portlet registered multiple keys) 
				if (restoreOnly && prevInstance != null && prevInstance.getRestoreColumn() == dbInstance.getRestoreColumn() && prevInstance.getRestoreRow() == dbInstance.getRestoreRow())
					continue;
				prevInstance = dbInstance;
				
				// Return this instance
				list.add(DbUserCache.getInstance(dbInstance));
				
				// If we have a limit, stop when reached
				if (maxCount > 0 && list.size() >= maxCount)
					break;
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return list;
	}
}
