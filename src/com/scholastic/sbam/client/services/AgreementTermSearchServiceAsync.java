package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.AgreementTermTuple;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

public interface AgreementTermSearchServiceAsync {

	void searchAgreementTerms(LoadConfig config, long syncId, AsyncCallback<SynchronizedPagingLoadResult<AgreementTermTuple>> callback);

}
