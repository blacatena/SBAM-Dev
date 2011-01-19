package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.PrefCodeSelCodeValidationService;
import com.scholastic.sbam.server.validation.AppPreferenceCodeValidator;
import com.scholastic.sbam.shared.objects.BetterRowEditInstance;
import com.scholastic.sbam.shared.objects.PreferenceCodeInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class PrefCodeSelCodeValidationServiceImpl extends FieldValidationServiceImpl implements PrefCodeSelCodeValidationService {
	
	protected void doValidation(String value, BetterRowEditInstance original, AsyncValidationResponse response) {
		AppPreferenceCodeValidator validator = new AppPreferenceCodeValidator();
		validator.setOriginal((PreferenceCodeInstance) original);
		response.addMessages(validator.validatePreferenceSelectionCode(value));
	}
	
	@Override
	protected String getAuthRole() {
		return SecurityManager.ROLE_CONFIG;
	}
}
