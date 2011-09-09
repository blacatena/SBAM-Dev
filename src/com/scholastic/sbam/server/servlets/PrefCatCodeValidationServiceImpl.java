package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.PrefCatCodeValidationService;
import com.scholastic.sbam.server.validation.AppPreferenceCategoryValidator;
import com.scholastic.sbam.shared.objects.BetterRowEditInstance;
import com.scholastic.sbam.shared.objects.PreferenceCategoryInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class PrefCatCodeValidationServiceImpl extends FieldValidationServiceImpl implements PrefCatCodeValidationService {
	
	protected void doValidation(String value, BetterRowEditInstance original, AsyncValidationResponse response) {
		AppPreferenceCategoryValidator validator = new AppPreferenceCategoryValidator();
		validator.setOriginal((PreferenceCategoryInstance) original);
		response.addErrorMessages(validator.validatePreferenceCategoryCode(value, original.isNewRecord()));
	}
	
	@Override
	protected String getAuthRole() {
		return SecurityManager.ROLE_CONFIG;
	}
}
