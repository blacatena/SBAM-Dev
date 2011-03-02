package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.AgreementTypeInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateAgreementType")
public interface UpdateAgreementTypeService extends RemoteService {
	UpdateResponse<AgreementTypeInstance> updateAgreementType(AgreementTypeInstance instance) throws IllegalArgumentException;
}
