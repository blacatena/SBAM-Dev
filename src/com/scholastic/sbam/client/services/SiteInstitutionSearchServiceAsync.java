package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

public interface SiteInstitutionSearchServiceAsync {

	void getSiteInstitutions(PagingLoadConfig loadConfig, String filter, boolean includeAgreementSummaries, long syncId, AsyncCallback<SynchronizedPagingLoadResult<InstitutionInstance>> callback);

}
