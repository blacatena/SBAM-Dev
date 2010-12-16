package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UserMessageCollection;

public interface UserMessageServiceAsync {

	void getUserMessages(String locationTag, AsyncCallback<UserMessageCollection> callback);

}
