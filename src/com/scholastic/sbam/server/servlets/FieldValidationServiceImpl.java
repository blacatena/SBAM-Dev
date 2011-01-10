package com.scholastic.sbam.server.servlets;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.objects.BetterRowEditInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

/**
 * This class acts as a template for most field validation services.
 * 
 * The programmer should implement the doValidation method to perform the actual validation, adding messages to the passed AsyncValidationResponse instance.
 * 
 * getAuthRole may be overridden to require a different authentication role.
 * 
 * authenticate may be overridden to perform entirely different authentication logic.
 * 
 * The validate method generally should not be overridden, but could be.
 */
@SuppressWarnings("serial")
public abstract class FieldValidationServiceImpl extends RemoteServiceServlet {

	/**
	 * Perform backend validation on a field value, given a previous data instance.
	 * 
	 * The 
	 * @param value
	 * The current value of the field to be validated.
	 * @param dataInstance
	 * The data instance representing the row values when editing began (and therefore probably reflecting the current values in the database).
	 * 
	 * Generally, this data can be used to determine if a data entry represents a new record or an update to an existing row.
	 * @param validationCounter
	 * A counter used to synchronize a response with the most recent validation call (all responses previous to the current client value will be ignored).
	 * @return
	 * An AsyncValidationResponse method identifying the response through the validation counter, and including any error messages.
	 * @throws Exception
	 */
	public AsyncValidationResponse validate(String value, BetterRowEditInstance dataInstance, final int validationCounter) throws Exception {
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		AsyncValidationResponse response = new AsyncValidationResponse(validationCounter);
		try {
			
			authenticate();

			doValidation(value, dataInstance, response);

		} catch (Exception exc) {
			exc.printStackTrace();
			throw exc;
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return response;
	}
	
	/**
	 * This may be overridden to provide different authentication, but most implementations will override getAuthRole() to return a value other than ROLE_ADMIN.
	 * @throws Exception
	 */
	protected void authenticate() throws Exception {
		String authUserName = null;
		Authentication auth = ((Authentication) getServletContext().getAttribute(SecurityManager.AUTHENTICATION_ATTRIBUTE));
		if (auth != null)
			authUserName = auth.getUserName();
		if (auth == null || authUserName == null || authUserName.length() == 0)
			throw new Exception("No validation privileges.");
		if (!auth.hasRoleName(getAuthRole()))
			throw new Exception("User does not have validation privileges.");
	}
	
	/**
	 * Implement this method to perform any validation.
	 * @param value
	 * The value currently entered in the field.
	 * @param original
	 * The original values of the data instance being edited.
	 * @param response
	 * The validation response which is going to be returned (and to which messages should be added).
	 */
	protected abstract void doValidation(String value, BetterRowEditInstance original, AsyncValidationResponse response);
	
	/**
	 * Override this method to return a different authentication role.
	 * 
	 * The default role is ROLE_ADMIN.
	 * @return
	 */
	protected String getAuthRole() {
		return SecurityManager.ROLE_ADMIN;
	}
}
