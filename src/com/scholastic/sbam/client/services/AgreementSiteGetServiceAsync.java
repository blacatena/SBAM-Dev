package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.AgreementSiteTuple;

public interface AgreementSiteGetServiceAsync {

	void getAgreementSite(int agreementId, int ucn, int ucnSuffix, String siteLocCode, boolean loadTerms, boolean allTerms, AsyncCallback<AgreementSiteTuple> callback);

}
