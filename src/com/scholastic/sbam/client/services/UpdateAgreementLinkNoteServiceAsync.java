package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.AgreementLinkInstance;

public interface UpdateAgreementLinkNoteServiceAsync {

	void updateAgreementLinkNote(AgreementLinkInstance beanModel, AsyncCallback<UpdateResponse<AgreementLinkInstance>> callback);

}
