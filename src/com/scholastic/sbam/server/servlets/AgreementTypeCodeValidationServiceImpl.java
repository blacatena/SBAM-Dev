package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.AgreementTypeCodeValidationService;
import com.scholastic.sbam.server.validation.AppAgreementTypeValidator;
import com.scholastic.sbam.shared.objects.BetterRowEditInstance;
import com.scholastic.sbam.shared.objects.AgreementTypeInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AgreementTypeCodeValidationServiceImpl extends FieldValidationServiceImpl implements AgreementTypeCodeValidationService {
	
	protected void doValidation(String value, BetterRowEditInstance original, AsyncValidationResponse response) {
		AppAgreementTypeValidator validator = new AppAgreementTypeValidator();
		validator.setOriginal((AgreementTypeInstance) original);
		response.addErrorMessages(validator.validateAgreementTypeCode(value, original.isNewRecord()));
	}
	
	@Override
	protected String getAuthRole() {
		return SecurityManager.ROLE_CONFIG;
	}
}
