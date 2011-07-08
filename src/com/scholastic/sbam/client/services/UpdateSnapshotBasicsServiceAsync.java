package com.scholastic.sbam.client.services;

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.SnapshotInstance;

public interface UpdateSnapshotBasicsServiceAsync {
	void updateSnapshotBasics(int snapshotId, String snapshotName, char snapshotStatus, Date expireDatetime, String note, AsyncCallback<UpdateResponse<SnapshotInstance>> callback);
}
