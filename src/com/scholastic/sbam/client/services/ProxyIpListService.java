package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.ProxyIpInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getProxyIps")
public interface ProxyIpListService extends RemoteService {
	List<ProxyIpInstance> getProxyIps(int proxyId, long loIp, long hiIp, char neStatus) throws IllegalArgumentException;
}
