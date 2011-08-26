package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.ProxyInstance;

public interface UpdateProxyNoteServiceAsync {

	void updateProxyNote(ProxyInstance beanModel, AsyncCallback<UpdateResponse<ProxyInstance>> callback);

}
