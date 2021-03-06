package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.AgreementNotesTuple;

public interface AgreementNotesGetServiceAsync {

	void getAgreementNotes(int agreementId, boolean loadTerms, boolean allTerms, AsyncCallback<AgreementNotesTuple> callback);

}
