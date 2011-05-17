package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.AgreementTermTuple;

public interface AgreementTermGetServiceAsync {

	void getAgreementTerm(int agreementId, int termId, boolean loadTerms, boolean allTerms, AsyncCallback<AgreementTermTuple> callback);

}
