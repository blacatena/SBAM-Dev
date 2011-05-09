package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.SiteInstance;

public interface UpdateSiteLocationNoteServiceAsync {

	void updateSiteLocationNote(SiteInstance beanModel, AsyncCallback<UpdateResponse<SiteInstance>> callback);

}
