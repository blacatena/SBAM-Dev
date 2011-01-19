package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.PreferenceCodeInstance;

public interface UpdatePreferenceCodeServiceAsync {

	void updatePreferenceCode(PreferenceCodeInstance beanModel, AsyncCallback<UpdateResponse<PreferenceCodeInstance>> callback);

}
