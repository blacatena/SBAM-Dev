package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.ProxyIpInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateProxyIpAddress")
public interface UpdateProxyIpAddressService extends RemoteService {
	UpdateResponse<ProxyIpInstance> updateProxyIpAddress(ProxyIpInstance instance) throws IllegalArgumentException;
}
