package com.scholastic.sbam.server.servlets;

import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.scholastic.sbam.client.services.AuthenticateService;
import com.scholastic.sbam.server.database.codegen.User;
import com.scholastic.sbam.server.database.codegen.UserRole;
import com.scholastic.sbam.server.database.objects.DbUser;
import com.scholastic.sbam.server.database.objects.DbUserRole;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AuthenticateServiceImpl extends RemoteServiceServlet implements AuthenticateService {

	@Override
	public Authentication authenticate(String userName, String password) throws IllegalArgumentException {
		Authentication auth = new Authentication();
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		User user = DbUser.getByUserName(userName);	//	DbUserHelper.findByUserName(userName);
		if (user == null || !user.getPassword().equals(password)) {
			auth.setMessage("Invalid user name or password.");
			auth.setAuthenticated(false);
			this.getServletContext().setAttribute(SecurityManager.AUTHENTICATION_ATTRIBUTE, null);
		} else {
			auth.setUserName(userName);
			auth.setFirstName(user.getFirstName());
			auth.setLastName(user.getLastName());
			auth.setAuthenticated(true);
			auth.setMessage("");
			loadRoles(auth);
			
			this.getServletContext().setAttribute(SecurityManager.AUTHENTICATION_ATTRIBUTE, auth);
			
			user.setLoginCount(user.getLoginCount() + 1);
			DbUser.persist(user);	//	DbUserHelper.persist(user);
		}
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return auth;
	}
	
	private void loadRoles(Authentication auth) {
		List<UserRole> roles = DbUserRole.findByUserName(auth.getUserName());
		for (UserRole role: roles) {
			auth.addRoleName(role.getId().getRoleName());
		}
	}
}
