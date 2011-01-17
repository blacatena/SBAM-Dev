package com.scholastic.sbam.client.services;

import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.CancelReasonInstance;

public interface CancelReasonListServiceAsync {

	void getCancelReasons(LoadConfig loadConfig, AsyncCallback<List<CancelReasonInstance>> callback);

}
