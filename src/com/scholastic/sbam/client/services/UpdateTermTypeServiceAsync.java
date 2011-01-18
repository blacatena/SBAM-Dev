package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.TermTypeInstance;

public interface UpdateTermTypeServiceAsync {

	void updateTermType(TermTypeInstance beanModel, AsyncCallback<UpdateResponse<TermTypeInstance>> callback);

}
