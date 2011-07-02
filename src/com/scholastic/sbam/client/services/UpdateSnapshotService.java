package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.SnapshotInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateSnapshot")
public interface UpdateSnapshotService extends RemoteService {
	UpdateResponse<SnapshotInstance> updateSnapshot(SnapshotInstance instance) throws IllegalArgumentException;
}
