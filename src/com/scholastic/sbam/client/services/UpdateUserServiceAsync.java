package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.UserInstance;

public interface UpdateUserServiceAsync {

	void updateUser(UserInstance beanModel, AsyncCallback<UpdateResponse<UserInstance>> callback);

}
