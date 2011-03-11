package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UserCacheTarget;

public interface UpdateUserCacheServiceAsync {

	void updateUserCache(UserCacheTarget beanModel, AsyncCallback<String> callback);

}
