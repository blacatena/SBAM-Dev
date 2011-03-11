package com.scholastic.sbam.client.services;

import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UserCacheInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getUserCaches")
public interface UserCacheListService extends RemoteService {
	List<UserCacheInstance> getUserCacheTargets(LoadConfig loadConfig, String userName, String category, Date fromDate, int maxCount, boolean restoreOnly) throws IllegalArgumentException;
}
