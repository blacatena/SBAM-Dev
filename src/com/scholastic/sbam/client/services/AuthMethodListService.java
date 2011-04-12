package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getAuthMethods")
public interface AuthMethodListService extends RemoteService {
	List<AuthMethodInstance> getAuthMethods(int agreementId, int ucn, int ucnSuffix, String siteLocCode, String methodType, char neStatus) throws IllegalArgumentException;
}
