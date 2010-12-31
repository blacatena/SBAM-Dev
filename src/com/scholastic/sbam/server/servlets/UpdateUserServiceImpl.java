package com.scholastic.sbam.server.servlets;

import java.text.SimpleDateFormat;
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
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.UserInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.validation.AppRoleGroupValidator;
import com.scholastic.sbam.shared.validation.AppUserNameValidator;
import com.scholastic.sbam.shared.validation.EmailValidator;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateUserServiceImpl extends RemoteServiceServlet implements UpdateUserService {

	@Override
	public UpdateResponse<UserInstance> updateUser(UserInstance instance) throws IllegalArgumentException {
		
		boolean newCreated				= false;
		boolean passwordReset			= false;
		
		String	messages				= null;
		
		User dbInstance = null;
		
		Authentication auth = (Authentication) getServletContext().getAttribute(SecurityManager.AUTHENTICATION_ATTRIBUTE);
		//	If user is no longer logged in, just skip this update
		if (auth == null)
			throw new IllegalArgumentException("Requesting user is not authenticated.");
		if (!auth.hasRoleName(SecurityManager.ROLE_ADMIN))
			throw new IllegalArgumentException("Requesting user is not authenticated for this task.");
		
		if (instance.getUserName() == null)
			throw new IllegalArgumentException("A user name is required.");
		useValidators(instance);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Pre-edit/fix values
			
			//	Get existing, or create new
			if (instance.getId() != null) {
				dbInstance = DbUser.getById(instance.getId());
			}

			//	If none found, create new
			if (dbInstance == null) {
				newCreated = true;
				dbInstance = new User();
				//	Set the create date/time
				dbInstance.setCreatedDatetime(new Date());
				//	Set a random password
				SimpleDateFormat fmt = new SimpleDateFormat("mmss");
				dbInstance.setPassword(instance.getUserName() + fmt.format(new Date()));
			}

			//	Update values
			if (instance.getUserName() != null)
				dbInstance.setUserName(instance.getUserName());
			if (instance.isResetPassword()) {
				passwordReset = true;
				messages = "The user's password has been reset, and an e-mail has been sent to " + instance.getEmail() + ".";
				SimpleDateFormat fmt = new SimpleDateFormat("mmss");
				dbInstance.setPassword(instance.getUserName() + fmt.format(new Date()));
			}
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
			
			//	If the user changed his own capabilities, make them take effect for this session
			if (instance.getUserName().equals(auth.getUserName())) {
				System.out.println("Refresh authentication");
				System.out.println(auth);
				AuthenticateServiceImpl.doAuthentication(instance.getUserName(), instance.getPassword(), this.getServletContext());
				System.out.println(getServletContext().getAttribute(SecurityManager.AUTHENTICATION_ATTRIBUTE));
			}
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		if (newCreated) {
			//	Send new e-mail
		} else if (passwordReset) {
			///	Send password reset e-mail
		}
		
		return new UpdateResponse<UserInstance>(instance, messages);
	}
	
	private void useValidators(UserInstance instance) throws IllegalArgumentException {
		testMessage(new AppUserNameValidator().validate(instance.getUserName()));
	//	testMessage(new AppPasswordValidator().validate(instance.getPassword()));
		testMessage(new EmailValidator().validate(instance.getEmail()));
		testMessage(new AppRoleGroupValidator().validate(instance.getRoleGroupTitle()));
	}
	
	private void testMessage(String message) throws IllegalArgumentException {
		if (message != null)
			throw new IllegalArgumentException(message);
	}
	
	private void silentRollback() {
		try {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.getSession().getTransaction().rollback();	
		} catch (Exception exc) { }
	}
	
	private void setRoles(UserInstance instance) throws Exception {
		//	If instance is deleted, don't bother with this... leave their roles as is
		if (instance.getStatus() == 'X')
			return;
		
		String [] roleNames = SecurityManager.getRoleNames(instance.getRoleGroupTitle());
		if (roleNames.length == 0)
			throw new IllegalArgumentException("Invalid role group '" + instance.getRoleGroupTitle() + "'.");
		
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
