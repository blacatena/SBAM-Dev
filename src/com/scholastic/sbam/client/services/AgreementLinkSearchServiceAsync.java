package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.AgreementLinkInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

public interface AgreementLinkSearchServiceAsync {

	void searchAgreementLinks(PagingLoadConfig loadConfig, String filter, long syncId, AsyncCallback<SynchronizedPagingLoadResult<AgreementLinkInstance>> callback);

}
