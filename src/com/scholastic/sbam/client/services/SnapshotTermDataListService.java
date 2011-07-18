package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.SnapshotTermDataInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getSnapshotTermData")
public interface SnapshotTermDataListService extends RemoteService {
	SynchronizedPagingLoadResult<SnapshotTermDataInstance> getSnapshotTermData(PagingLoadConfig loadConfig, int snapshotId, long syncId) throws IllegalArgumentException, ServiceNotReadyException;
}
