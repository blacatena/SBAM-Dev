package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.ProxyTuple;

public interface ProxyGetServiceAsync {

	void getProxy(int proxyId, boolean getIps, AsyncCallback<ProxyTuple> callback);

}
