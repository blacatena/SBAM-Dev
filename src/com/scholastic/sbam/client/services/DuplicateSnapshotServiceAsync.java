package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.SnapshotInstance;

public interface DuplicateSnapshotServiceAsync {
	void duplicateSnapshot(int snapshotId, String snapshotName, AsyncCallback<UpdateResponse<SnapshotInstance>> callback);
}
