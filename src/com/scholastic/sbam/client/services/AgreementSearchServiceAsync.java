package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

public interface AgreementSearchServiceAsync {

	void searchAgreements(LoadConfig config, AgreementInstance sampleInstance, boolean currentTerms, long syncId, AsyncCallback<SynchronizedPagingLoadResult<AgreementInstance>> callback);

}
