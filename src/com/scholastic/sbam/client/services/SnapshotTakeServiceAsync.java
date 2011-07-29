package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.SnapshotInstance;

public interface SnapshotTakeServiceAsync {
	void takeSnapshot(int snapshotId, AsyncCallback<SnapshotInstance> callback);
}
