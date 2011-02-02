package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.CommissionCodeValidationService;
import com.scholastic.sbam.server.validation.AppCommissionTypeValidator;
import com.scholastic.sbam.shared.objects.BetterRowEditInstance;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class CommissionCodeValidationServiceImpl extends FieldValidationServiceImpl implements CommissionCodeValidationService {
	
	protected void doValidation(String value, BetterRowEditInstance original, AsyncValidationResponse response) {
		AppCommissionTypeValidator validator = new AppCommissionTypeValidator();
		validator.setOriginal((CommissionTypeInstance) original);
		response.addMessages(validator.validateCommissionCode(value, original.isNewRecord()));
	}
	
	@Override
	protected String getAuthRole() {
		return SecurityManager.ROLE_CONFIG;
	}
}
