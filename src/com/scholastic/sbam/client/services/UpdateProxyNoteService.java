package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.ProxyInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateProxyNote")
public interface UpdateProxyNoteService extends RemoteService {
	UpdateResponse<ProxyInstance> updateProxyNote(ProxyInstance instance) throws IllegalArgumentException;
}
