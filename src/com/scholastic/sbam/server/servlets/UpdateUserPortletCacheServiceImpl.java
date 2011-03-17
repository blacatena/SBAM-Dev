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
		
		if (instance == null) {
			throw new IllegalArgumentException("Attempt to update null portlet instance.");
		}
		
		UserPortletCache dbInstance = null;
		
		// This is a DIRTY fix... after logging out, some updates still try to happen.
		// For now, catch these errors and don't throw them
		Authentication auth = null;
		try {
			auth = authenticate("update user portlet cache", SecurityManager.ROLE_QUERY);
		} catch (IllegalArgumentException e) {
			return "";
		}
		
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
				dbInstance = new UserPortletCache();
				UserPortletCacheId dbId = new UserPortletCacheId();
				dbId.setUserName(auth.getUserName());
				dbId.setPortletId(instance.getPortletId());
				dbInstance.setId(dbId);
			}
			
			//	If the portlet isn't ready to save yet, and wasn't found, there's nothing more to do
			if (dbInstance == null)
				return "";
			
			if (instance.getPortletType() != null)
				dbInstance.setPortletType(instance.getPortletType());
			dbInstance.setMinimized(instance.getMinimized());
			dbInstance.setRestoreColumn(instance.getRestoreColumn());
			dbInstance.setRestoreRow(instance.getRestoreRow());
			dbInstance.setRestoreWidth(instance.getRestoreWidth());
			dbInstance.setRestoreHeight(instance.getRestoreHeight());
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
