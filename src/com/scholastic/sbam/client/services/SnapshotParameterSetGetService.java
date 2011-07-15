package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.SnapshotParameterSetInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getSnapshotParameterSet")
public interface SnapshotParameterSetGetService extends RemoteService {
	SnapshotParameterSetInstance getSnapshotParameterSet(int snapshotId, String source) throws IllegalArgumentException, ServiceNotReadyException;
}
