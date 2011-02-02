package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.ProductCodeValidationService;
import com.scholastic.sbam.server.validation.AppProductValidator;
import com.scholastic.sbam.shared.objects.BetterRowEditInstance;
import com.scholastic.sbam.shared.objects.ProductInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ProductCodeValidationServiceImpl extends FieldValidationServiceImpl implements ProductCodeValidationService {
	
	protected void doValidation(String value, BetterRowEditInstance original, AsyncValidationResponse response) {
		AppProductValidator validator = new AppProductValidator();
		validator.setOriginal((ProductInstance) original);
		response.addMessages(validator.validateProductCode(value, original.isNewRecord()));
	}
	
	@Override
	protected String getAuthRole() {
		return SecurityManager.ROLE_CONFIG;
	}
}
