package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.Authentication;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("authenticate")
public interface AuthenticateService extends RemoteService {
	Authentication authenticate(String username, String password) throws IllegalArgumentException;
}
