package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface WelcomeMessageServiceAsync {

	void getWelcomeMessages(AsyncCallback<String> callback);

}
