package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.UpdateUserPortletCacheService;
import com.scholastic.sbam.server.database.codegen.UserPortletCache;
import com.scholastic.sbam.server.database.codegen.UserPortletCacheId;
import com.scholastic.sbam.server.database.objects.DbUserPortletCache;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.objects.UserPortletCacheInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * Update the user cache in the database to reflect access to a cached target item.
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateUserPortletCacheServiceImpl extends AuthenticatedServiceServlet implements UpdateUserPortletCacheService {

	@Override
	public String updateUserPortletCache(UserPortletCacheInstance instance) throws IllegalArgumentException {
		
		UserPortletCache dbInstance = null;
		
		Authentication auth = authenticate("update user portlet cache", SecurityManager.ROLE_QUERY);
		
		if (auth.getUserName() == null || auth.getUserName().length() == 0)
			throw new IllegalArgumentException("No user name associated with user portlet cache transaction.");
		if (instance.getPortletId() < 0)
			throw new IllegalArgumentException("No portlet ID associated with user portlet cache transaction.");
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		try {
			
			dbInstance = DbUserPortletCache.getById(auth.getUserName(), instance.getPortletId());
			
			//	If none found, create new
			if (dbInstance == null && instance.getRestoreColumn() >= 0 && instance.getRestoreRow() >= 0) {
				System.out.println("Not found.");
				dbInstance = new UserPortletCache();
				UserPortletCacheId dbId = new UserPortletCacheId();
				dbId.setUserName(auth.getUserName());
				dbId.setPortletId(instance.getPortletId());
				dbInstance.setId(dbId);
			} else System.out.println("Found");
			
			dbInstance.setPortletType(instance.getPortletType());
			dbInstance.setMinimized(instance.getMinimized());
			dbInstance.setRestoreColumn(instance.getRestoreColumn());
			dbInstance.setRestoreRow(instance.getRestoreRow());
			dbInstance.setKeyData(instance.getKeyData());
			
			//	Persist in database
			if (dbInstance != null) {
				if (instance.getRestoreColumn() < 0 && instance.getRestoreRow() < 0)	// Means delete it
					DbUserPortletCache.delete(dbInstance);
				else
					DbUserPortletCache.persist(dbInstance);
			}
			
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The user portlet cache update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		//	There's no interaction here, so the response is irrelevant
		return "";
	}
	
	private void silentRollback() {
		try {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.getSession().getTransaction().rollback();	
		} catch (Exception exc) { }
	}
}
