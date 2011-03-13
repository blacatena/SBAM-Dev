package com.scholastic.sbam.client.services;

import java.util.Date;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.objects.UserCacheInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getUserCacheTargets")
public interface UserCacheListService extends RemoteService {
	SynchronizedPagingLoadResult<UserCacheInstance> getUserCacheTargets(PagingLoadConfig loadConfig, String userName, String category, Date fromDate, int maxCount, long syncId) throws IllegalArgumentException;
}
