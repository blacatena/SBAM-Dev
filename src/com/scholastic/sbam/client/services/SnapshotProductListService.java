package com.scholastic.sbam.client.services;

import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.SnapshotProductTreeInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getSnapshotProducts")
public interface SnapshotProductListService extends RemoteService {
	List<SnapshotProductTreeInstance> getSnapshotProducts(int snapshotId, LoadConfig loadConfig) throws IllegalArgumentException;
}
