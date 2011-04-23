package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

public interface SiteLocationValidationServiceAsync {

	void validateSiteLocation(int ucn, int ucnSuffix, String siteLocCode, int validationCount, AsyncCallback<AsyncValidationResponse> callback);

}
