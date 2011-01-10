package com.scholastic.sbam.server.servlets;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.scholastic.sbam.client.services.UserNameValidationService;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.objects.BetterRowEditInstance;
import com.scholastic.sbam.shared.objects.UserInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UserNameValidationServiceImpl extends RemoteServiceServlet implements UserNameValidationService {

	@Override
	public AsyncValidationResponse validate(String value, BetterRowEditInstance dataInstance, final int validationCounter) throws IllegalArgumentException {
		System.out.println("UserNameValidationService");
		System.out.println(dataInstance);
		
		UserInstance original = (UserInstance) dataInstance;
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		AsyncValidationResponse response = new AsyncValidationResponse(validationCounter);
		try {
			String authUserName = null;
			Authentication auth = ((Authentication) getServletContext().getAttribute(SecurityManager.AUTHENTICATION_ATTRIBUTE));
			if (auth != null)
				authUserName = auth.getUserName();
			if (auth == null || authUserName == null || authUserName.length() == 0)
				throw new Exception("No validation privilegess.");
			if (!auth.hasRoleName(SecurityManager.ROLE_ADMIN))
				throw new Exception("User does not have validation privileges.");

			if (original.getId() != null && original.getId() > 0) {
				// Existing user edits
				if (original.getUserName() != value)
					response.setMessage("User name cannot be changed.");
			} else {
				//	New user edits
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return response;
	}
}
