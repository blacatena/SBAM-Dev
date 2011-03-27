package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.AgreementInstance;

public interface UpdateAgreementNoteServiceAsync {

	void updateAgreementNote(AgreementInstance beanModel, AsyncCallback<UpdateResponse<AgreementInstance>> callback);

}
