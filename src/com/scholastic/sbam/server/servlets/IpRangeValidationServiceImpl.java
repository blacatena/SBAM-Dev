package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.IpRangeValidationService;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.validation.AppIpConflictDetector;
import com.scholastic.sbam.shared.objects.MethodIdInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

/**
 * This class performs a validation of an ip address range for a method instance (and agreement method, site method, or proxy) to determine if there
 * are any conflicts, i.e. situations where this address conflicts with another address for a different use, site, or range.
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

			new AppIpConflictDetector(ipLo, ipHi, methodId, response).doValidation();

		} catch (Exception exc) {
			exc.printStackTrace();
			throw exc;
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return response;
	}
}
