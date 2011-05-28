package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.AuthMethodTuple;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getAuthMethod")
public interface AuthMethodGetService extends RemoteService {
	AuthMethodTuple getAuthMethod(int agreementId, int ucn, int ucnSuffix, String siteLocCode, String methodType, int methodKey, boolean loadTerms, boolean allTerms) throws IllegalArgumentException;
}
