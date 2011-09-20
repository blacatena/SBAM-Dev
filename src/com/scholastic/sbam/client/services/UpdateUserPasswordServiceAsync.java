package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UpdateUserPasswordServiceAsync {

	void updateUserPassword(String user, String oldPassword, String newPassword, AsyncCallback<String> callback);

}
