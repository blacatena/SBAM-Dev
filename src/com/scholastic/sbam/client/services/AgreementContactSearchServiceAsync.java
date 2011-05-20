package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.AgreementContactTuple;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

public interface AgreementContactSearchServiceAsync {

	void searchAgreementContacts(LoadConfig config, long syncId, AsyncCallback<SynchronizedPagingLoadResult<AgreementContactTuple>> callback);

}
