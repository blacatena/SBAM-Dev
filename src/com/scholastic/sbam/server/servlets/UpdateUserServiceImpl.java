package com.scholastic.sbam.server.servlets;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.scholastic.sbam.client.services.UpdateUserService;
import com.scholastic.sbam.server.database.codegen.User;
import com.scholastic.sbam.server.database.codegen.UserRole;
import com.scholastic.sbam.server.database.codegen.UserRoleId;
import com.scholastic.sbam.server.database.objects.DbUser;
import com.scholastic.sbam.server.database.objects.DbUserRole;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.util.MailHelper;
import com.scholastic.sbam.server.validation.AppUserValidator;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.UserInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateUserServiceImpl extends AuthenticatedServiceServlet implements UpdateUserService {

	@Override
	public UpdateResponse<UserInstance> updateUser(UserInstance instance) throws IllegalArgumentException {
		
		boolean newCreated				= false;
		boolean passwordReset			= false;
		
		String	messages				= null;
		
		User dbInstance = null;
		
		Authentication auth = authenticate("update users", SecurityManager.ROLE_ADMIN);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Pre-edit/fix values
			validateInput(instance);
			
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
				messages = "The user " + instance.getUserName() + "'s password has been reset, and an e-mail has been sent to " + instance.getEmail() + ".";
				int suffix = (int) (1000 * Math.random());
			//	SimpleDateFormat fmt = new SimpleDateFormat("mmss");
				dbInstance.setPassword(instance.getUserName() + suffix); // fmt.format(new Date()));
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
			
			//	Refresh when new row is created, to get assigned ID
			if (newCreated) {
			//	DbUser.refresh(dbInstance);	// This may not be necessary, but just in case
				instance.setId(dbInstance.getId());
				instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
			}
			
			//	If the user changed his own capabilities, make them take effect for this session
			if (instance.getUserName().equals(auth.getUserName())) {
				AuthenticateServiceImpl.doAuthentication(instance.getUserName(), instance.getPassword(), this.getServletContext());
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
			sendEmail(dbInstance, "Scholastic SBAM New User ID", "usercreated.txt");
		} else if (passwordReset) {
			///	Send password reset e-mail
			sendEmail(dbInstance, "Scholastic SBAM Password Reset", "passwordReset.txt");
		}
		
		return new UpdateResponse<UserInstance>(instance, messages);
	}
	
	private void validateInput(UserInstance instance) throws IllegalArgumentException {
//		testMessage(new AppUserNameValidator().validate(instance.getUserName()));
//	//	testMessage(new AppPasswordValidator().validate(instance.getPassword()));
//		testMessage(new EmailValidator().validate(instance.getEmail()));
//		testMessage(new AppRoleGroupValidator().validate(instance.getRoleGroupTitle()));
		AppUserValidator validator = new AppUserValidator();
		validator.setOriginal(instance);	//	This isn't really the original, but it's good enough, because it has the original ID
		testMessages(validator.validateUser(instance));
	}
	
	private void testMessages(List<String> messages) throws IllegalArgumentException {
		if (messages != null)
			for (String message: messages)
				testMessage(message);
	}
	
	private void testMessage(String message) throws IllegalArgumentException {
		if (message != null && message.length() > 0)
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
		if (roleNames == null)
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
	
	private void sendEmail(User user, String subject, String emailTemplate) {
		try {
			MailHelper.sendStandardMailFromFile(user, subject, emailTemplate);	
		} catch (Exception e) {
			System.out.println("ERROR sending password reset e-mail to " + user.getEmail());
			System.out.println(e);
			e.printStackTrace();
		}
	}
}
