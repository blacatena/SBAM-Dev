package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.DeleteReasonInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateDeleteReason")
public interface UpdateDeleteReasonService extends RemoteService {
	UpdateResponse<DeleteReasonInstance> updateDeleteReason(DeleteReasonInstance instance) throws IllegalArgumentException;
}
