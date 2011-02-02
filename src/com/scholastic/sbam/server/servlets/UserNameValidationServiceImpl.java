package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.UserNameValidationService;
import com.scholastic.sbam.server.validation.AppUserValidator;
import com.scholastic.sbam.shared.objects.BetterRowEditInstance;
import com.scholastic.sbam.shared.objects.UserInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UserNameValidationServiceImpl extends FieldValidationServiceImpl implements UserNameValidationService {
	
	protected void doValidation(String value, BetterRowEditInstance original, AsyncValidationResponse response) {
		AppUserValidator validator = new AppUserValidator();
		validator.setOriginal((UserInstance) original);
		response.addMessages(validator.validateUserName(value, original.isNewRecord()));
	}
	
	@Override
	protected String getAuthRole() {
		return SecurityManager.ROLE_ADMIN;
	}
}
