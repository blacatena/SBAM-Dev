package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.SiteInstance;

public interface UpdateSiteLocationServiceAsync {

	void updateSiteLocation(SiteInstance beanModel, AsyncCallback<UpdateResponse<SiteInstance>> callback);

}
