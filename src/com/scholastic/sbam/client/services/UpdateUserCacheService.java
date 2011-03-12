package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UserCacheTarget;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateUserCache")
public interface UpdateUserCacheService extends RemoteService {
	String updateUserCache(UserCacheTarget target, String hint) throws IllegalArgumentException;
}
