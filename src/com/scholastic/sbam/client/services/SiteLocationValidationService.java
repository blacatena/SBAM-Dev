package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("validateSiteLocation")
public interface SiteLocationValidationService extends RemoteService {
	AsyncValidationResponse validateSiteLocation(int ucn, int ucnSuffix, String siteLocCode, int validationCount) throws IllegalArgumentException, Exception;
}
