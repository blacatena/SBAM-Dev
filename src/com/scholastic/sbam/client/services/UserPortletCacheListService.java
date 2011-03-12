package com.scholastic.sbam.client.services;

import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UserPortletCacheInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getUserPortlets")
public interface UserPortletCacheListService extends RemoteService {
	List<UserPortletCacheInstance> getUserPortlets(LoadConfig loadConfig, String userName) throws IllegalArgumentException;
}
