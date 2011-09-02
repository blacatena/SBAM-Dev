package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.UrlValidationService;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.validation.AppUrlConflictDetector;
import com.scholastic.sbam.shared.objects.MethodIdInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

/**
 * This class performs a validation of a referrer URL for a method instance (and agreement method or site method) to determine if there
 * are any conflicts, i.e. situations where this URL conflicts with the same URL for a different use, site, or range.
 * 
 */
@SuppressWarnings("serial")
public class UrlValidationServiceImpl extends AuthenticatedServiceServlet implements UrlValidationService {

	/**
	 * Perform backend validation on a field value, given a previous data instance.
	 * 
	 * The 
	 * @param ipLo
	 * The low IP address.
	 * @param ipHi
	 * The high IP address
	 * @param validationCounter
	 * A counter used to synchronize a response with the most recent validation call (all responses previous to the current client value will be ignored).
	 * @return
	 * An AsyncValidationResponse method identifying the response through the validation counter, and including any error messages.
	 * @throws Exception
	 */
	public AsyncValidationResponse validateUrl(String url, MethodIdInstance methodId, final int validationCounter) throws Exception {

		authenticate("validate referrer URL", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		AsyncValidationResponse response = new AsyncValidationResponse(validationCounter);
		try {
			
			authenticate();

			new AppUrlConflictDetector(url, methodId, response).doValidation();

		} catch (Exception exc) {
			exc.printStackTrace();
			throw exc;
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return response;
	}
}
