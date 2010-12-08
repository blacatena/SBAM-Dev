package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DeauthenticateServiceAsync {

	void deauthenticate(AsyncCallback<String> callback);

}
