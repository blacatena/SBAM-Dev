package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.PreferenceCategoryInstance;

public interface UpdatePreferenceCategoryServiceAsync {

	void updatePreferenceCategory(PreferenceCategoryInstance beanModel, AsyncCallback<UpdateResponse<PreferenceCategoryInstance>> callback);

}
