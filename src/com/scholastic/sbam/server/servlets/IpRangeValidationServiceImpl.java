package com.scholastic.sbam.server.servlets;

import java.util.List;

import com.scholastic.sbam.client.services.IpRangeValidationService;
import com.scholastic.sbam.server.database.codegen.AuthMethod;
import com.scholastic.sbam.server.database.objects.DbAuthMethod;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;
import com.scholastic.sbam.shared.objects.MethodIdInstance;
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
public class IpRangeValidationServiceImpl extends AuthenticatedServiceServlet implements IpRangeValidationService {

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
	public AsyncValidationResponse validateIpRange(long ipLo, long ipHi, MethodIdInstance methodId, final int validationCounter) throws Exception {

		authenticate("validate ip range", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		AsyncValidationResponse response = new AsyncValidationResponse(validationCounter);
		try {
			
			authenticate();

			doValidation(ipLo, ipHi, methodId, response);

		} catch (Exception exc) {
			exc.printStackTrace();
			throw exc;
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return response;
	}

	private void doValidation(long ipLo, long ipHi, MethodIdInstance methodId, AsyncValidationResponse response) {
		System.out.println("Validating ip " + ipLo + " to " + ipHi + " try " + response.getValidationCounter()); 
		
		List<AuthMethod> authMethods  = DbAuthMethod.findOverlapIps(ipLo, ipHi);
		
		if (authMethods != null) {
			for (AuthMethod authMethod : authMethods) {
				AuthMethodInstance amInstance = DbAuthMethod.getInstance(authMethod);
				//	Don't check a method against itself
				if (methodId != null && methodId.equals(amInstance.obtainMethodId())) {
					System.out.println("Skip " + amInstance + " : " + amInstance.getMethodDisplay());
					continue;
				}
				System.out.println("Test " + amInstance + " : " + amInstance.getMethodDisplay());
			}
		}
		
		/* TODO
		 * Proxy Ips
		 */
	}
}
