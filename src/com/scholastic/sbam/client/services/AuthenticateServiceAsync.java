package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.Authentication;

public interface AuthenticateServiceAsync {

	void authenticate(String username, String password, AsyncCallback<Authentication> callback);

}
