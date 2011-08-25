package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.AgreementLinkTuple;

public interface AgreementLinkGetServiceAsync {

	void getAgreementLink(int linkId, boolean loadAgreements, boolean allAgreements, AsyncCallback<AgreementLinkTuple> callback);

}
