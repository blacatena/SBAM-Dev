package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.AgreementLinkInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateAgreementLink")
public interface UpdateAgreementLinkService extends RemoteService {
	UpdateResponse<AgreementLinkInstance> updateAgreementLink(AgreementLinkInstance instance) throws IllegalArgumentException;
}
