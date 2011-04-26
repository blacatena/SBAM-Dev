package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.ProxyInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateProxy")
public interface UpdateProxyService extends RemoteService {
	UpdateResponse<ProxyInstance> updateProxy(ProxyInstance instance) throws IllegalArgumentException;
}
