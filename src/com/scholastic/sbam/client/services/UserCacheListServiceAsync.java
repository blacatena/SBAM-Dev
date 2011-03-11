package com.scholastic.sbam.client.services;

import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UserCacheInstance;

public interface UserCacheListServiceAsync {

	void getUserCacheTargets(LoadConfig loadConfig, String userName, String category, Date fromDate, int maxCount, boolean restoreOnly, AsyncCallback<List<UserCacheInstance>> callback);

}
