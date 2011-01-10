package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.UserNameValidationService;
import com.scholastic.sbam.server.validation.AppUserValidator;
import com.scholastic.sbam.shared.objects.BetterRowEditInstance;
import com.scholastic.sbam.shared.objects.UserInstance;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UserNameValidationServiceImpl extends FieldValidationServiceImpl implements UserNameValidationService {

//	@Override
//	public AsyncValidationResponse validate(String value, BetterRowEditInstance dataInstance, final int validationCounter) throws Exception {
//		System.out.println("UserNameValidationService");
//		System.out.println(dataInstance);
//		
//		UserInstance original = (UserInstance) dataInstance;
//		
//		HibernateUtil.openSession();
//		HibernateUtil.startTransaction();
//
//		AsyncValidationResponse response = new AsyncValidationResponse(validationCounter);
//		try {
//			
//			authenticate();
//
//			doValidation(value, original, response);
//
//		} catch (Exception exc) {
//			exc.printStackTrace();
//			throw exc;
//		}
//		
//		HibernateUtil.endTransaction();
//		HibernateUtil.closeSession();
//		
//		return response;
//	}
	
	protected void doValidation(String value, BetterRowEditInstance original, AsyncValidationResponse response) {
		AppUserValidator validator = new AppUserValidator();
		validator.setOriginal((UserInstance) original);
		response.addMessages(validator.validateUserName(value));
	}
}
