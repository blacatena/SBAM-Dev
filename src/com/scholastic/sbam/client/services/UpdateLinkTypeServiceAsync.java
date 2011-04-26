package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.LinkTypeInstance;

public interface UpdateLinkTypeServiceAsync {

	void updateLinkType(LinkTypeInstance beanModel, AsyncCallback<UpdateResponse<LinkTypeInstance>> callback);

}
