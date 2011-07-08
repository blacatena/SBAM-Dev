package com.scholastic.sbam.client.services;

import java.util.Date;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.SnapshotInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateSnapshotBasics")
public interface UpdateSnapshotBasicsService extends RemoteService {
	UpdateResponse<SnapshotInstance> updateSnapshotBasics(int snapshotId, String snapshotName, char snapshotStatus, Date expireDatetime, String note) throws IllegalArgumentException;
}
