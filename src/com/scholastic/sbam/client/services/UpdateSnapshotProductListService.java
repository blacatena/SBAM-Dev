package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.SnapshotProductTreeInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateSnapshotProductList")
public interface UpdateSnapshotProductListService extends RemoteService {
	String updateSnapshotProductList(int snapshotId, boolean updateOrg, List<SnapshotProductTreeInstance> list) throws IllegalArgumentException;
}
