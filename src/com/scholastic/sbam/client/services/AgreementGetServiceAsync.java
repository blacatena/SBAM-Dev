package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.AgreementInstance;

public interface AgreementGetServiceAsync {

	void getAgreement(int agreementId, boolean allTerms, AsyncCallback<AgreementInstance> callback);

}
