package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.AgreementSiteInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateAgreementSiteNote")
public interface UpdateAgreementSiteNoteService extends RemoteService {
	UpdateResponse<AgreementSiteInstance> updateAgreementSiteNote(AgreementSiteInstance instance) throws IllegalArgumentException;
}
