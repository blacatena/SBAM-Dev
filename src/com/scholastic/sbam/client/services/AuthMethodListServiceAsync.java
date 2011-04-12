package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;

public interface AuthMethodListServiceAsync {

	void getAuthMethods(int agreementId, int ucn, int ucnSuffix, String siteLocCode, String methodType, char neStatus, AsyncCallback<List<AuthMethodInstance>> callback);

}
