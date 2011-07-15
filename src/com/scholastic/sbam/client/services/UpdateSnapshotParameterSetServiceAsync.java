package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.SnapshotParameterSetInstance;

public interface UpdateSnapshotParameterSetServiceAsync {

	void updateSnapshotParameterSet(SnapshotParameterSetInstance parameterSet, AsyncCallback<String> callback);

}
