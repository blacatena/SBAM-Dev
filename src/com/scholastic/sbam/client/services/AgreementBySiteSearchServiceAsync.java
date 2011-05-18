package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.AgreementSiteTuple;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

public interface AgreementBySiteSearchServiceAsync {

	void searchAgreementsBySite(LoadConfig config, long syncId, AsyncCallback<SynchronizedPagingLoadResult<AgreementSiteTuple>> callback);

}
