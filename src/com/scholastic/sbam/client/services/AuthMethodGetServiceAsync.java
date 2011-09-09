package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.AuthMethodTuple;

public interface AuthMethodGetServiceAsync {

	void getAuthMethod(int agreementId, int ucn, int ucnSuffix, String siteLocCode, String methodType, int methodKey, boolean loadTerms, boolean allTerms, boolean conflicts, AsyncCallback<AuthMethodTuple> callback);

}
