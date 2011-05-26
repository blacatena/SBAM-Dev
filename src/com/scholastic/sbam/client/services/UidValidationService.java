package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.MethodIdInstance;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("validateUid")
public interface UidValidationService extends RemoteService {
	AsyncValidationResponse validateUid(String uid, String password, char userType, int proxyId, MethodIdInstance methodId, int validationCount) throws IllegalArgumentException, Exception;
}
