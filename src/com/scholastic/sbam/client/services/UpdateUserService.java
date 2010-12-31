package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.UserInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateUser")
public interface UpdateUserService extends RemoteService {
	UpdateResponse<UserInstance> updateUser(UserInstance instance) throws IllegalArgumentException;
}
