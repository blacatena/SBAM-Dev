package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.LinkTypeCodeValidationService;
import com.scholastic.sbam.server.validation.AppLinkTypeValidator;
import com.scholastic.sbam.shared.objects.BetterRowEditInstance;
import com.scholastic.sbam.shared.objects.LinkTypeInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class LinkTypeCodeValidationServiceImpl extends FieldValidationServiceImpl implements LinkTypeCodeValidationService {
	
	protected void doValidation(String value, BetterRowEditInstance original, AsyncValidationResponse response) {
		AppLinkTypeValidator validator = new AppLinkTypeValidator();
		validator.setOriginal((LinkTypeInstance) original);
		response.addErrorMessages(validator.validateLinkTypeCode(value, original.isNewRecord()));
	}
	
	@Override
	protected String getAuthRole() {
		return SecurityManager.ROLE_CONFIG;
	}
}
