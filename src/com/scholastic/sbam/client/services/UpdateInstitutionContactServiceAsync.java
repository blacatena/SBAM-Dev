package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.InstitutionContactInstance;

public interface UpdateInstitutionContactServiceAsync {

	void updateInstitutionContact(InstitutionContactInstance beanModel, AsyncCallback<UpdateResponse<InstitutionContactInstance>> callback);

}
