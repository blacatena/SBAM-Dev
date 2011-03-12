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
		
		UserCache dbInstance = null;
		
		Authentication auth = authenticate("update user cache", SecurityManager.ROLE_QUERY);
		
		if (auth.getUserName() == null || auth.getUserName().length() == 0)
			throw new IllegalArgumentException("No user name associated with user cache transaction.");
		if (instance.userCacheCategory() == null || instance.userCacheCategory().length() == 0)
			throw new IllegalArgumentException("No cache category associated with user cache transaction.");
		if (instance.userCacheIntegerKey() < 0 && (instance.userCacheStringKey() == null || instance.userCacheStringKey().length() == 0))
			throw new IllegalArgumentException("No key associated with user cache transaction.");
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			
			dbInstance = DbUserCache.getById(auth.getUserName(), instance.userCacheCategory(), instance.userCacheIntegerKey(), instance.userCacheStringKey());
			
			//	If none found, create new
			if (dbInstance == null) {
				dbInstance = new UserCache();
				UserCacheId dbId = new UserCacheId();
				dbId.setUserName(auth.getUserName());
				dbId.setCategory(instance.userCacheCategory());
				dbId.setIntKey(instance.userCacheIntegerKey());
				if (instance.userCacheStringKey() == null)
					dbId.setStrKey("");	//	Null not allowed
				else
					dbId.setStrKey(instance.userCacheStringKey());
				dbInstance.setId(dbId);
			}
			
			dbInstance.setAccessDatetime(new Date());
			
			if (hint == null)
				hint = "";
			dbInstance.setHint(hint);
			
			//	Persist in database
			DbUserCache.persist(dbInstance);
			
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
	
	private void silentRollback() {
		try {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.getSession().getTransaction().rollback();	
		} catch (Exception exc) { }
	}
}
