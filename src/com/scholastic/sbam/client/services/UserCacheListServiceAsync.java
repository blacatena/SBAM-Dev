package com.scholastic.sbam.client.services;

import java.util.Date;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.objects.UserCacheInstance;

public interface UserCacheListServiceAsync {

	void getUserCacheTargets(PagingLoadConfig loadConfig, String userName, String category, Date fromDate, int maxCount, long syncId, AsyncCallback<SynchronizedPagingLoadResult<UserCacheInstance>> callback);

}
