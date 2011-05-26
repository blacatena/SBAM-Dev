package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.MethodIdInstance;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

public interface UrlValidationServiceAsync {
	void validateUrl(String url, MethodIdInstance methodId, int validationCounter, AsyncCallback<AsyncValidationResponse> callback);
}
