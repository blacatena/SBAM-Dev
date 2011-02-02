package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.DeleteReasonCodeValidationService;
import com.scholastic.sbam.server.validation.AppDeleteReasonValidator;
import com.scholastic.sbam.shared.objects.BetterRowEditInstance;
import com.scholastic.sbam.shared.objects.DeleteReasonInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class DeleteReasonCodeValidationServiceImpl extends FieldValidationServiceImpl implements DeleteReasonCodeValidationService {
	
	protected void doValidation(String value, BetterRowEditInstance original, AsyncValidationResponse response) {
		AppDeleteReasonValidator validator = new AppDeleteReasonValidator();
		validator.setOriginal((DeleteReasonInstance) original);
		response.addMessages(validator.validateDeleteReasonCode(value, original.isNewRecord()));
	}
	
	@Override
	protected String getAuthRole() {
		return SecurityManager.ROLE_CONFIG;
	}
}
