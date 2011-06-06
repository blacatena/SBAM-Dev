package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.AgreementRemoteSetupUrlTuple;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getAgreementRemoteSetupUrl")
public interface AgreementRemoteSetupUrlGetService extends RemoteService {
	AgreementRemoteSetupUrlTuple getAgreementRemoteSetupUrl(int agreementId, int contactId, boolean loadTerms, boolean allTerms) throws IllegalArgumentException;
}
