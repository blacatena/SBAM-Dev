package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.SnapshotParameterSetInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateSnapshotParameterSet")
public interface UpdateSnapshotParameterSetService extends RemoteService {
	String updateSnapshotParameterSet(SnapshotParameterSetInstance parameterSet) throws IllegalArgumentException;
}
