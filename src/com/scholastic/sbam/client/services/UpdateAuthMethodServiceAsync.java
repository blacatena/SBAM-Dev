package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;

public interface UpdateAuthMethodServiceAsync {

	void updateAuthMethod(AuthMethodInstance beanModel, AsyncCallback<UpdateResponse<AuthMethodInstance>> callback);

}
