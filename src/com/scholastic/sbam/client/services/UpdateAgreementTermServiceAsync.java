package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;

public interface UpdateAgreementTermServiceAsync {

	void updateAgreementTerm(AgreementTermInstance beanModel, AsyncCallback<UpdateResponse<AgreementTermInstance>> callback);

}
