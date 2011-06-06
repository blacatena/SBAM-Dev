package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.AgreementRemoteSetupUrlTuple;

public interface AgreementRemoteSetupUrlGetServiceAsync {

	void getAgreementRemoteSetupUrl(int agreementId, int contactId, boolean loadTerms, boolean allTerms, AsyncCallback<AgreementRemoteSetupUrlTuple> callback);

}
