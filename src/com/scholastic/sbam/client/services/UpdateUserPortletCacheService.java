package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UserPortletCacheInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateUserPortletCache")
public interface UpdateUserPortletCacheService extends RemoteService {
	String updateUserPortletCache(UserPortletCacheInstance instance) throws IllegalArgumentException;
}
