package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.AgreementRemoteSetupUrlTuple;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

public interface AgreementRemoteSetupUrlSearchServiceAsync {

	void searchAgreementRemoteSetupUrls(LoadConfig config, long syncId, AsyncCallback<SynchronizedPagingLoadResult<AgreementRemoteSetupUrlTuple>> callback);

}
