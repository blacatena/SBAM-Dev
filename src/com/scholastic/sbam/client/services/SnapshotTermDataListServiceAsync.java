package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.SnapshotTermDataInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

public interface SnapshotTermDataListServiceAsync {

	void getSnapshotTermData(PagingLoadConfig loadConfig, int snapshotId, long syncId, AsyncCallback<SynchronizedPagingLoadResult<SnapshotTermDataInstance>> callback);

}
