package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.MethodIdInstance;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("validateUrl")
public interface UrlValidationService extends RemoteService {
	AsyncValidationResponse validateUrl(String url, MethodIdInstance methodId, int validationCount) throws IllegalArgumentException, Exception;
}
