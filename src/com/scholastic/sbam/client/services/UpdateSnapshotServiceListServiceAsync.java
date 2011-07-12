package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.SnapshotServiceTreeInstance;

public interface UpdateSnapshotServiceListServiceAsync {

	void updateSnapshotServiceList(int snapshotId, boolean updateOrg, List<SnapshotServiceTreeInstance> list, AsyncCallback<String> callback);

}
