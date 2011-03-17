package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.ContactTypeCodeValidationService;
import com.scholastic.sbam.server.validation.AppContactTypeValidator;
import com.scholastic.sbam.shared.objects.BetterRowEditInstance;
import com.scholastic.sbam.shared.objects.ContactTypeInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ContactTypeCodeValidationServiceImpl extends FieldValidationServiceImpl implements ContactTypeCodeValidationService {
	
	protected void doValidation(String value, BetterRowEditInstance original, AsyncValidationResponse response) {
		AppContactTypeValidator validator = new AppContactTypeValidator();
		validator.setOriginal((ContactTypeInstance) original);
		response.addMessages(validator.validateContactTypeCode(value, original.isNewRecord()));
	}
	
	@Override
	protected String getAuthRole() {
		return SecurityManager.ROLE_CONFIG;
	}
}
