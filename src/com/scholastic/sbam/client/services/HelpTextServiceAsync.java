package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.HelpTextInstance;

public interface HelpTextServiceAsync {

	void getHelpText(String id, AsyncCallback<HelpTextInstance> callback);

}
