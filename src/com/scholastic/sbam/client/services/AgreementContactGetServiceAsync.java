package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.AgreementContactTuple;

public interface AgreementContactGetServiceAsync {

	void getAgreementContact(int agreementId, int contactId, boolean loadTerms, boolean allTerms, AsyncCallback<AgreementContactTuple> callback);

}
