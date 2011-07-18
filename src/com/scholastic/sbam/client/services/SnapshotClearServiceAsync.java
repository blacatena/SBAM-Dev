package com.scholastic.sbam.client.services;

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SnapshotClearServiceAsync {
	void clearSnapshot(int snapshotId, Date snapshotTaken, AsyncCallback<String> callback);
}
