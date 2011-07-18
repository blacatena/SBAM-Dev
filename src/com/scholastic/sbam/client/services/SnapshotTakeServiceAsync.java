package com.scholastic.sbam.client.services;

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SnapshotTakeServiceAsync {
	void takeSnapshot(int snapshotId, AsyncCallback<Date> callback);
}
