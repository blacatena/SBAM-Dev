package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.ProxyIpInstance;

public interface UpdateProxyIpAddressNoteServiceAsync {

	void updateProxyIpAddressNote(ProxyIpInstance beanModel, AsyncCallback<UpdateResponse<ProxyIpInstance>> callback);

}
