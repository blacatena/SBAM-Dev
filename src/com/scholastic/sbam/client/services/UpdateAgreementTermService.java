package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateAgreementTerm")
public interface UpdateAgreementTermService extends RemoteService {
	UpdateResponse<AgreementTermInstance> updateAgreementTerm(AgreementTermInstance instance) throws IllegalArgumentException;
}
