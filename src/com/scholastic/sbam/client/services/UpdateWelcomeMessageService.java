package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.WelcomeMessageInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateWelcomeMessage")
public interface UpdateWelcomeMessageService extends RemoteService {
	UpdateResponse<WelcomeMessageInstance> updateWelcomeMessage(WelcomeMessageInstance instance) throws IllegalArgumentException;
}
