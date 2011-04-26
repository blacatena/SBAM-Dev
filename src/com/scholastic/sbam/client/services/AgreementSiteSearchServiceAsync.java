package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.AgreementSiteInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

public interface AgreementSiteSearchServiceAsync {

	void searchAgreementSites(PagingLoadConfig loadConfig, int agreementId, String filter, char neStatus, long syncId, AsyncCallback<SynchronizedPagingLoadResult<AgreementSiteInstance>> callback);

}
