package com.scholastic.sbam.client.services;

import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.CacheStatusInstance;

public interface CacheStatusListServiceAsync {

	void listCacheStatus(LoadConfig loadConfig, AsyncCallback<List<CacheStatusInstance>> callback);

}