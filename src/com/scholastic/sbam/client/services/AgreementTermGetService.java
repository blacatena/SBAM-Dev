package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.AgreementTermTuple;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getAgreementTerm")
public interface AgreementTermGetService extends RemoteService {
	AgreementTermTuple getAgreementTerm(int agreementId, int termId, boolean loadTerms, boolean allTerms) throws IllegalArgumentException;
}
