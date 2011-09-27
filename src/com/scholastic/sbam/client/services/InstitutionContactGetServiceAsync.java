package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.InstitutionContactTuple;

public interface InstitutionContactGetServiceAsync {

	void getInstitutionContact(int ucn, int contactId, boolean includeAgreementSummaries, AsyncCallback<InstitutionContactTuple> callback);

}
