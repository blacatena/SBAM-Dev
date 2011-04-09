package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.AgreementContactInstance;

public interface UpdateAgreementContactServiceAsync {

	void updateAgreementContact(AgreementContactInstance beanModel, AsyncCallback<UpdateResponse<AgreementContactInstance>> callback);

}
