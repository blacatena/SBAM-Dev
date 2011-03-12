package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.scholastic.sbam.client.services.UserPortletCacheListService;
import com.scholastic.sbam.server.database.codegen.UserPortletCache;
import com.scholastic.sbam.server.database.objects.DbUserPortletCache;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.objects.UserPortletCacheInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UserPortletCacheListServiceImpl extends AuthenticatedServiceServlet implements UserPortletCacheListService {

	@Override
	public List<UserPortletCacheInstance> getUserPortlets(LoadConfig loadConfig, String userName) throws IllegalArgumentException {
		
		Authentication auth = authenticate("list user portlets",	SecurityManager.ROLE_QUERY);
		
		if (userName == null)
			userName = auth.getUserName();
		
		//	Need another level of security to look at someone else's cache
		if (!auth.getUserName().equals(userName))
			auth = authenticate("list foreign user portlets", SecurityManager.ROLE_ADMIN);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<UserPortletCacheInstance> list = new ArrayList<UserPortletCacheInstance>();
		try {
			
			List<UserPortletCache> dbInstances = DbUserPortletCache.findByUserName(userName);

			for (UserPortletCache dbInstance : dbInstances) {
				list.add(DbUserPortletCache.getInstance(dbInstance));
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return list;
	}
}
