package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.AgreementSiteInstance;

public interface AgreementSiteListServiceAsync {

	void getAgreementSites(int agreementId, char neStatus, AsyncCallback<List<AgreementSiteInstance>> callback);

}
