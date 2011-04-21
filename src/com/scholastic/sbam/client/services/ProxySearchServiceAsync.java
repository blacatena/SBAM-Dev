package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.ProxyInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

public interface ProxySearchServiceAsync {

	void searchProxies(PagingLoadConfig loadConfig, String filter, long syncId, AsyncCallback<SynchronizedPagingLoadResult<ProxyInstance>> callback);

}
