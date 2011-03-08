package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.AgreementInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getAgreement")
public interface AgreementGetService extends RemoteService {
	AgreementInstance getAgreement(int agreementId, boolean allTerms) throws IllegalArgumentException;
}
