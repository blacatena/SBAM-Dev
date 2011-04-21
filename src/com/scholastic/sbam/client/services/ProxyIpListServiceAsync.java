package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.ProxyIpInstance;

public interface ProxyIpListServiceAsync {

	void getProxyIps(int proxyId, long loIp, long hiIp, char neStatus, AsyncCallback<List<ProxyIpInstance>> callback);

}
