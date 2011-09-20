package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.UpdateUserPasswordService;
import com.scholastic.sbam.server.database.codegen.User;
import com.scholastic.sbam.server.database.objects.DbUser;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.util.MailHelper;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateUserPasswordServiceImpl extends AuthenticatedServiceServlet implements UpdateUserPasswordService {

	@Override
	public String updateUserPassword(String user, String oldPassword, String newPassword) throws IllegalArgumentException {
		
		User dbInstance = null;
		
		Authentication auth = authenticate("update user password", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			if (user == null || user.length() == 0)
				user = auth.getUserName();
			if (!auth.getUserName().equals(user))
				throw new IllegalArgumentException("You cannot change another user's password.");
					
			//	Get existing, or create new
			dbInstance = DbUser.getByUserName(user);

			//	If none found, create new
			if (dbInstance == null) {
				throw new IllegalArgumentException("User name not found.");
			}
			
			if (!dbInstance.getPassword().equals(oldPassword))
				throw new IllegalArgumentException("Invalid old password.");

			//	Update values
			dbInstance.setPassword(newPassword);
			
			//	Persist in database
			DbUser.persist(dbInstance);
			
			
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
		
		sendEmail(dbInstance, "Scholastic SBAM Password Change", "passwordChange.txt");
		
		return "Your password has been changed.";
	}
	
	protected void silentRollback() {
		try {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.getSession().getTransaction().rollback();	
		} catch (Exception exc) { }
	}
	
	protected void sendEmail(User user, String subject, String emailTemplate) {
		try {
			MailHelper.sendStandardMailFromFile(user, subject, emailTemplate);	
		} catch (Exception e) {
			System.out.println("ERROR sending password change e-mail to " + user.getEmail());
			System.out.println(e);
			e.printStackTrace();
		}
	}
}
