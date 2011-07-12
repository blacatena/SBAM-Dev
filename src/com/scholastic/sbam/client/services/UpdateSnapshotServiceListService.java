package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.SnapshotServiceTreeInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateSnapshotServiceList")
public interface UpdateSnapshotServiceListService extends RemoteService {
	String updateSnapshotServiceList(int snapshotId, boolean updateOrg, List<SnapshotServiceTreeInstance> list) throws IllegalArgumentException;
}
