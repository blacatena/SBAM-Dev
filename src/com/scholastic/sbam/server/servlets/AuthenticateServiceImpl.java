package com.scholastic.sbam.server.servlets;

import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.scholastic.sbam.client.services.AuthenticateService;
import com.scholastic.sbam.server.database.codegen.User;
import com.scholastic.sbam.server.database.codegen.UserPortletCache;
import com.scholastic.sbam.server.database.codegen.UserProfile;
import com.scholastic.sbam.server.database.codegen.UserRole;
import com.scholastic.sbam.server.database.objects.DbUser;
import com.scholastic.sbam.server.database.objects.DbUserPortletCache;
import com.scholastic.sbam.server.database.objects.DbUserProfile;
import com.scholastic.sbam.server.database.objects.DbUserRole;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * Log a user in.
 * 
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AuthenticateServiceImpl extends RemoteServiceServlet implements AuthenticateService {

	@Override
	public Authentication authenticate(String userName, String password) throws IllegalArgumentException {
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		Authentication auth =  doAuthentication(userName, password, this.getServletContext());
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return auth;
	}
	
	/**
	 * This method performs authentication within an existing database transaction (which is why it is a static method).
	 * 
	 * It may be used by other classes when authentication must be refreshed as part of a separate transaction.
	 * 
	 * @param userName
	 * @param password
	 * @param context
	 * @return
	 */
	public static Authentication doAuthentication(String userName, String password, ServletContext context) {
		//	Removing any ' characters prevents anyone from hacking into the database through SQL Injection attacks
		userName = userName.replace("'","");
		
		Authentication auth = new Authentication();
		
		User user = DbUser.getByUserName(userName);	//	DbUserHelper.findByUserName(userName);
		if (user == null || !user.getPassword().equals(password)) {
			auth.setMessage("Invalid user name or password.");
			auth.setAuthenticated(false);
			context.setAttribute(SecurityManager.AUTHENTICATION_ATTRIBUTE, null);
		} else {
			auth.setUserName(userName);
			auth.setFirstName(user.getFirstName());
			auth.setLastName(user.getLastName());
			auth.setAuthenticated(true);
			auth.setMessage("");
			loadRoles(auth);
			
			context.setAttribute(SecurityManager.AUTHENTICATION_ATTRIBUTE, auth);
			
			user.setLoginCount(user.getLoginCount() + 1);
			DbUser.persist(user);	//	DbUserHelper.persist(user);
			
			//	To optimize loading cached portlets, get them now and count them, but don't include the actual list in the authentication data
			List<UserPortletCache> portlets = DbUserPortletCache.findByUserName(userName);
			auth.setCachedPortlets(portlets.size());
			
			UserProfile profile = DbUserProfile.getByUserName(auth.getUserName());
			auth.setProfile(DbUserProfile.getInstance(profile));
			
			cleanUpUserCache(auth);
			
			System.out.println("Logged in " + userName + " at " + new Date());
		}
		
		return auth;
	}
	
	private static void loadRoles(Authentication auth) {
		List<UserRole> roles = DbUserRole.findByUserName(auth.getUserName());
		for (UserRole role: roles) {
			auth.addRoleName(role.getId().getRoleName());
		}
	}
	
	private static void cleanUpUserCache(Authentication auth) {
		if (auth == null)
			return;
		
		
		/**
		 * TODO
		 * Delete cacheList entries that are too old, or if there are too many.
		 */
//		List<UserCache> cacheList = DbUserCache.findByUserName(auth.getUserName());
	}
}
