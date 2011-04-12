package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateAuthMethod")
public interface UpdateAuthMethodService extends RemoteService {
	UpdateResponse<AuthMethodInstance> updateAuthMethod(AuthMethodInstance instance) throws IllegalArgumentException;
}
