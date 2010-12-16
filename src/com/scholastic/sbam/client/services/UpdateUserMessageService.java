package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UserMessageInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateUserMessage")
public interface UpdateUserMessageService extends RemoteService {
	Integer updateUserMessage(UserMessageInstance instance) throws IllegalArgumentException;
}
