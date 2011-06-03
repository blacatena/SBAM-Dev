package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.RemoteSetupUrlInstance;

public interface RemoteSetupUrlListServiceAsync {

	void getRemoteSetupUrls(int agreementId, int ucn, int ucnSuffix, String siteLocCode, char neStatus, AsyncCallback<List<RemoteSetupUrlInstance>> callback);

}
