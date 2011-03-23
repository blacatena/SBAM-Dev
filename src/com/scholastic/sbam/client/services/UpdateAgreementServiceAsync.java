package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.AgreementInstance;

public interface UpdateAgreementServiceAsync {

	void updateAgreement(AgreementInstance beanModel, AsyncCallback<UpdateResponse<AgreementInstance>> callback);

}
