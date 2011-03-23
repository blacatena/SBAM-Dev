package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.AgreementInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateAgreement")
public interface UpdateAgreementService extends RemoteService {
	UpdateResponse<AgreementInstance> updateAgreement(AgreementInstance instance) throws IllegalArgumentException;
}
