package com.scholastic.sbam.client.services;

import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.SnapshotTreeInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getSnapshots")
public interface SnapshotListService extends RemoteService {
	List<SnapshotTreeInstance> getSnapshots(String snapshotType, LoadConfig loadConfig) throws IllegalArgumentException;
}
