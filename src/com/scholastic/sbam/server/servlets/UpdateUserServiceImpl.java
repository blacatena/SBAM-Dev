package com.scholastic.sbam.server.servlets;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.scholastic.sbam.client.services.UpdateUserService;
import com.scholastic.sbam.server.database.codegen.User;
import com.scholastic.sbam.server.database.codegen.UserRole;
import com.scholastic.sbam.server.database.codegen.UserRoleId;
import com.scholastic.sbam.server.database.objects.DbUser;
import com.scholastic.sbam.server.database.objects.DbUserRole;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.objects.UserInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateUserServiceImpl extends RemoteServiceServlet implements UpdateUserService {

	@Override
	public UserInstance updateUser(UserInstance instance) throws IllegalArgumentException {
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			Authentication auth = (Authentication) getServletContext().getAttribute(SecurityManager.AUTHENTICATION_ATTRIBUTE);
			//	If user is no longer logged in, just skip this update
			if (auth == null)
				throw new IllegalArgumentException("Requesting user is not authenticated.");
			if (!auth.hasRoleName(SecurityManager.ROLE_ADMIN))
				throw new IllegalArgumentException("Requesting user is not authenticated for this task.");
			
			//	Pre-edit/fix values
			if (instance.getUserName() == null)
				instance.setUserName("error");
			
			User dbInstance = null;
			
			//	Get existing, or create new
			if (instance.getId() != null) {
				dbInstance = DbUser.getById(instance.getId());
			}

			//	If none found, create new
			if (dbInstance == null) {
				dbInstance = new User();
				dbInstance.setCreatedDatetime(new Date());
			}

			//	Update values
			if (instance.getUserName() != null)
				dbInstance.setUserName(instance.getUserName());
			if (instance.getPassword() != null)
				dbInstance.setPassword(instance.getPassword());
			if (instance.getFirstName() != null)
				dbInstance.setFirstName(instance.getFirstName());
			if (instance.getLastName() != null)
				dbInstance.setLastName(instance.getLastName());
			if (instance.getEmail() != null)
				dbInstance.setEmail(instance.getEmail());
			
			dbInstance.setStatus(instance.getStatus());
			
			//	Persist in database
			DbUser.persist(dbInstance);
			
			//	Update roles
			setRoles(instance);
			
			//	Refresh when new row is created, to get assigend ID
			if (dbInstance.getId() == null) {
				DbUser.refresh(dbInstance);
				instance.setId(dbInstance.getId());
			}
			
			if (instance.getUserName().equals(auth.getUserName())) {
				System.out.println("Refresh authentication");
				System.out.println(auth);
				AuthenticateServiceImpl.doAuthentication(instance.getUserName(), instance.getPassword(), this.getServletContext());
				System.out.println(getServletContext().getAttribute(SecurityManager.AUTHENTICATION_ATTRIBUTE));
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return instance;
	}
	
	private void setRoles(UserInstance instance) throws Exception {
		String [] roleNames = SecurityManager.getRoleNames(instance.getRoleGroupTitle());
		if (roleNames.length == 0)
			throw new Exception("Invalid role group name " + instance.getRoleGroupTitle());
		
		List<UserRole> oldRoles = DbUserRole.findByUserName(instance.getUserName());
		for (UserRole role : oldRoles) {
			boolean delete = true;
			for (int i = 0; i < roleNames.length; i++)
				if (role.getId().getRoleName().equals(roleNames [i])) {
					delete = false;
					break;
				}
			if (delete) {
				DbUserRole.delete(role);
			}
		}
		
		for (int i = 0; i < roleNames.length; i++) {
			boolean add = true;
			for (UserRole role : oldRoles) {
				if (role.getId().getRoleName().equals(roleNames [i])) {
					add = false;
					break;
				}
			}
			if (add) {
				UserRole newRole = new UserRole();
				UserRoleId roleId = new UserRoleId();
				roleId.setUserName(instance.getUserName());
				roleId.setRoleName(roleNames [i]);
				newRole.setId(roleId);
				DbUserRole.persist(newRole);
			}
		}
	}
}
