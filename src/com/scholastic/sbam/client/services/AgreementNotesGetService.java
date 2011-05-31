package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.AgreementNotesTuple;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getAgreementNotes")
public interface AgreementNotesGetService extends RemoteService {
	AgreementNotesTuple getAgreementNotes(int agreementId, boolean loadTerms, boolean allTerms) throws IllegalArgumentException;
}
