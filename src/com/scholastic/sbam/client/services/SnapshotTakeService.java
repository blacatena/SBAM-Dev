package com.scholastic.sbam.client.services;

import java.util.Date;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("takeSnapshot")
public interface SnapshotTakeService extends RemoteService {
	Date takeSnapshot(int snapshotId) throws IllegalArgumentException;
}
