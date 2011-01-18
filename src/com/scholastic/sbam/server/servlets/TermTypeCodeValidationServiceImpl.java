package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.TermTypeCodeValidationService;
import com.scholastic.sbam.server.validation.AppTermTypeValidator;
import com.scholastic.sbam.shared.objects.BetterRowEditInstance;
import com.scholastic.sbam.shared.objects.TermTypeInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class TermTypeCodeValidationServiceImpl extends FieldValidationServiceImpl implements TermTypeCodeValidationService {
	
	protected void doValidation(String value, BetterRowEditInstance original, AsyncValidationResponse response) {
		AppTermTypeValidator validator = new AppTermTypeValidator();
		validator.setOriginal((TermTypeInstance) original);
		response.addMessages(validator.validateTermTypeCode(value));
	}
	
	@Override
	protected String getAuthRole() {
		return SecurityManager.ROLE_CONFIG;
	}
}
