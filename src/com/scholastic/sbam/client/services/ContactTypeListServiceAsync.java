package com.scholastic.sbam.client.services;

import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.ContactTypeInstance;

public interface ContactTypeListServiceAsync {

	void getContactTypes(LoadConfig loadConfig, AsyncCallback<List<ContactTypeInstance>> callback);

}
