package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UserMessageCollection;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getUserMessages")
public interface UserMessageService extends RemoteService {
	UserMessageCollection getUserMessages(String userName, String locationTag) throws IllegalArgumentException;
}
