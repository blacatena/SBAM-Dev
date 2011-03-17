package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.AgreementContactInstance;

public interface AgreementContactListServiceAsync {

	void getAgreementContacts(int agreementId, char neStatus, AsyncCallback<List<AgreementContactInstance>> callback);

}
