package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.SnapshotTreeInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateSnapshotTree")
public interface UpdateSnapshotTreeService extends RemoteService {
	String updateSnapshotTree(String snapshotType, List<SnapshotTreeInstance> list) throws IllegalArgumentException;
}
