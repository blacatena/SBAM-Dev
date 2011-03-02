package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.AgreementTypeInstance;

public interface UpdateAgreementTypeServiceAsync {

	void updateAgreementType(AgreementTypeInstance beanModel, AsyncCallback<UpdateResponse<AgreementTypeInstance>> callback);

}
