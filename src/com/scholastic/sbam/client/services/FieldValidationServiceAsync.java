package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.BetterRowEditInstance;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

public interface FieldValidationServiceAsync {

	void validate(String value, BetterRowEditInstance dataInstance, int validationCounter, AsyncCallback<AsyncValidationResponse> callback);

}
