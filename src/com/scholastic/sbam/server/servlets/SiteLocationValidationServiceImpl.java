package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.SiteLocationValidationService;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.validation.AppSiteValidator;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

/**
 * This validatoin just checks that the site location does not already exist in the database.
 */
@SuppressWarnings("serial")
public class SiteLocationValidationServiceImpl extends AuthenticatedServiceServlet implements SiteLocationValidationService {
	
	public AsyncValidationResponse validateSiteLocation(int ucn, int ucnSuffix, String siteLocCode, final int validationCounter) throws Exception {

		authenticate("validate fields", getAuthRole());
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		AsyncValidationResponse response = new AsyncValidationResponse(validationCounter);
		try {
			
			authenticate();

			doValidation(ucn, ucnSuffix, siteLocCode, response);

		} catch (Exception exc) {
			exc.printStackTrace();
			throw exc;
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return response;
	}
	
	protected void doValidation(int ucn, int ucnSuffix, String siteLocCode, AsyncValidationResponse response) {
		AppSiteValidator validator = new AppSiteValidator();
		response.addErrorMessages(validator.validateSiteId(ucn, ucnSuffix, siteLocCode, true));
	}

	protected String getAuthRole() {
		return SecurityManager.ROLE_QUERY;
	}
}
