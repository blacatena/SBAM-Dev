package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.ServiceCodeValidationService;
import com.scholastic.sbam.server.validation.AppServiceValidator;
import com.scholastic.sbam.shared.objects.BetterRowEditInstance;
import com.scholastic.sbam.shared.objects.ServiceInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ServiceCodeValidationServiceImpl extends FieldValidationServiceImpl implements ServiceCodeValidationService {
	
	protected void doValidation(String value, BetterRowEditInstance original, AsyncValidationResponse response) {
		AppServiceValidator validator = new AppServiceValidator();
		validator.setOriginal((ServiceInstance) original);
		response.addErrorMessages(validator.validateServiceCode(value, original.isNewRecord()));
	}
	
	@Override
	protected String getAuthRole() {
		return SecurityManager.ROLE_CONFIG;
	}
}
