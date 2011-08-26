package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.ProxyTuple;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getProxy")
public interface ProxyGetService extends RemoteService {
	ProxyTuple getProxy(int proxyId, boolean getIps) throws IllegalArgumentException;
}
