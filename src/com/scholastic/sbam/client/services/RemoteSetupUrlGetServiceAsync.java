package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.RemoteSetupUrlTuple;

public interface RemoteSetupUrlGetServiceAsync {

	void getRemoteSetupUrl(int agreementId, int ucn, int ucnSuffix, String siteLocCode, int urlId, boolean loadTerms, boolean allTerms, AsyncCallback<RemoteSetupUrlTuple> callback);

}
