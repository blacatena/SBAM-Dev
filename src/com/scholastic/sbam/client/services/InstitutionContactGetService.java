package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.InstitutionContactTuple;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getInstitutionContact")
public interface InstitutionContactGetService extends RemoteService {
	InstitutionContactTuple getInstitutionContact(int ucn, int contactId, boolean includeAgreementSummaries) throws IllegalArgumentException;
}
