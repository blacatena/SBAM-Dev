package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UserPortletCacheInstance;

public interface UpdateUserPortletCacheServiceAsync {

	void updateUserPortletCache(UserPortletCacheInstance instance, AsyncCallback<String> callback);

}
