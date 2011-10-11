package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.CacheStatusInstance;

public interface CacheRefreshServiceAsync {

	void refreshCache(String cacheKey, AsyncCallback<CacheStatusInstance> callback);

}