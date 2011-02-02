package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.CancelReasonCodeValidationService;
import com.scholastic.sbam.server.validation.AppCancelReasonValidator;
import com.scholastic.sbam.shared.objects.BetterRowEditInstance;
import com.scholastic.sbam.shared.objects.CancelReasonInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class CancelReasonCodeValidationServiceImpl extends FieldValidationServiceImpl implements CancelReasonCodeValidationService {
	
	protected void doValidation(String value, BetterRowEditInstance original, AsyncValidationResponse response) {
		AppCancelReasonValidator validator = new AppCancelReasonValidator();
		validator.setOriginal((CancelReasonInstance) original);
		response.addMessages(validator.validateCancelReasonCode(value, original.isNewRecord()));
	}
	
	@Override
	protected String getAuthRole() {
		return SecurityManager.ROLE_CONFIG;
	}
}
