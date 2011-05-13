package com.scholastic.sbam.server.servlets;

import java.util.Date;

import com.scholastic.sbam.client.services.UpdateUserCacheService;
import com.scholastic.sbam.server.database.codegen.UserCache;
import com.scholastic.sbam.server.database.codegen.UserCacheId;
import com.scholastic.sbam.server.database.objects.DbUserCache;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.objects.UserCacheTarget;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * Update the user cache in the database to reflect access to a cached target item.
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateUserCacheServiceImpl extends AuthenticatedServiceServlet implements UpdateUserCacheService {

	@Override
	public String updateUserCache(UserCacheTarget instance, String hint) throws IllegalArgumentException {
		
		Authentication auth = authenticate("update user cache", SecurityManager.ROLE_QUERY);
		
		if (auth.getUserName() == null || auth.getUserName().length() == 0)
			throw new IllegalArgumentException("No user name associated with user cache transaction.");
		for (int i = 0; i < instance.userCacheKeyCount(); i++) {
	 		if (instance.userCacheCategory(i) == null || instance.userCacheCategory(i).length() == 0)
				throw new IllegalArgumentException("No cache category associated with user cache transaction.");
			if (instance.userCacheIntegerKey(i) < 0 && (instance.userCacheStringKey(i) == null || instance.userCacheStringKey(i).length() == 0))
				throw new IllegalArgumentException("No key associated with user cache transaction.");
		}
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			for (int keySet = 0; keySet < instance.userCacheKeyCount(); keySet++)
				updateUserCache(auth.getUserName(), instance, keySet, hint);
			
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The user cache update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		//	There's no interaction here, so the response is irrelevant
		return "";
	}
	
	protected void updateUserCache(String userName, UserCacheTarget instance, int keySet, String hint) {
		UserCache dbInstance = DbUserCache.getById(userName, instance.userCacheCategory(keySet), instance.userCacheIntegerKey(keySet), instance.userCacheStringKey(keySet));
		
		//	If none found, create new
		if (dbInstance == null) {
			dbInstance = new UserCache();
			UserCacheId dbId = new UserCacheId();
			dbId.setUserName(userName);
			dbId.setCategory(instance.userCacheCategory(keySet));
			dbId.setIntKey(instance.userCacheIntegerKey(keySet));
			if (instance.userCacheStringKey(keySet) == null)
				dbId.setStrKey("");	//	Null not allowed
			else
				dbId.setStrKey(instance.userCacheStringKey(keySet));
			dbInstance.setId(dbId);
		}
		
		dbInstance.setAccessDatetime(new Date());
		
		if (hint == null)
			hint = "";
		dbInstance.setHint(hint);
		
		//	Persist in database
		DbUserCache.persist(dbInstance);
	}
	
	private void silentRollback() {
		try {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.getSession().getTransaction().rollback();	
		} catch (Exception exc) { }
	}
}
