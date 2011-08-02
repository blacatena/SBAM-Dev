package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.SnapshotProductTreeInstance;

public interface UpdateSnapshotProductListServiceAsync {

	void updateSnapshotProductList(int snapshotId, boolean updateOrg, List<SnapshotProductTreeInstance> list, AsyncCallback<String> callback);

}
