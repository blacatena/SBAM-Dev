package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.RemoteSetupUrlInstance;

public interface UpdateRemoteSetupUrlServiceAsync {

	void updateRemoteSetupUrl(RemoteSetupUrlInstance beanModel, AsyncCallback<UpdateResponse<RemoteSetupUrlInstance>> callback);

}
