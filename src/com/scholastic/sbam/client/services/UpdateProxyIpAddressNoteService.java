package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.ProxyIpInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateProxyIpAddressNote")
public interface UpdateProxyIpAddressNoteService extends RemoteService {
	UpdateResponse<ProxyIpInstance> updateProxyIpAddressNote(ProxyIpInstance instance) throws IllegalArgumentException;
}
