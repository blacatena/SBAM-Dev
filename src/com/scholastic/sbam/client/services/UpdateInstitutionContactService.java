package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.InstitutionContactInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateInstitutionContact")
public interface UpdateInstitutionContactService extends RemoteService {
	UpdateResponse<InstitutionContactInstance> updateInstitutionContact(InstitutionContactInstance instance) throws IllegalArgumentException;
}
