package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.AgreementContactTuple;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getAgreementContact")
public interface AgreementContactGetService extends RemoteService {
	AgreementContactTuple getAgreementContact(int agreementId, int contactId, boolean loadTerms, boolean allTerms) throws IllegalArgumentException;
}
