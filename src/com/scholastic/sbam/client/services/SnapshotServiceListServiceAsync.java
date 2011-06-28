package com.scholastic.sbam.client.services;

import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.ProductServiceTreeInstance;

public interface SnapshotServiceListServiceAsync {

	void getSnapshotServices(String snapshotCode, LoadConfig loadConfig, AsyncCallback<List<ProductServiceTreeInstance>> callback);

}
