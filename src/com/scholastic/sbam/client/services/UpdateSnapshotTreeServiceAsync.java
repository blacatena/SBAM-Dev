package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.SnapshotTreeInstance;

public interface UpdateSnapshotTreeServiceAsync {

	void updateSnapshotTree(String snapshotType, List<SnapshotTreeInstance> list, AsyncCallback<String> callback);

}
