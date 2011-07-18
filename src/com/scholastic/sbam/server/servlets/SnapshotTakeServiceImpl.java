package com.scholastic.sbam.server.servlets;

import java.util.Date;

import com.scholastic.sbam.client.services.SnapshotTakeService;
import com.scholastic.sbam.server.reporting.SnapshotMaker;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service to update just the name, status and/or note of a snapshot.
 */
@SuppressWarnings("serial")
public class SnapshotTakeServiceImpl extends AuthenticatedServiceServlet implements SnapshotTakeService {

	@Override
	public Date takeSnapshot(int snapshotId) throws IllegalArgumentException {
		
		if (snapshotId <= 0)
			throw new IllegalArgumentException("Snapshot data can only be compiled for an existing snapshot.");
		
//		Date snapshotTaken = new Date();
		
		authenticate("take (compile) snapshot", SecurityManager.ROLE_QUERY);
		
		return new SnapshotMaker().makeSnapshot(snapshotId);
	}
}
