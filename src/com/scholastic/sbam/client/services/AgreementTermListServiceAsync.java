package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;

public interface AgreementTermListServiceAsync {

	void getAgreementTerms(int agreementId, char neStatus, AsyncCallback<List<AgreementTermInstance>> callback);

}
