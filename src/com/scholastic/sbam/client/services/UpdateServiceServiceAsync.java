package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.ServiceInstance;

public interface UpdateServiceServiceAsync {

	void updateService(ServiceInstance beanModel, AsyncCallback<UpdateResponse<ServiceInstance>> callback);

}
