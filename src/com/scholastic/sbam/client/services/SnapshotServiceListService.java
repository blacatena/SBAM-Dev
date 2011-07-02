package com.scholastic.sbam.client.services;

import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.SnapshotServiceTreeInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getSnapshotServices")
public interface SnapshotServiceListService extends RemoteService {
	List<SnapshotServiceTreeInstance> getSnapshotServices(String snapshotCode, LoadConfig loadConfig) throws IllegalArgumentException;
}
