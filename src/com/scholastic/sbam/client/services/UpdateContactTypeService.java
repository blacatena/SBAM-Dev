package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.ContactTypeInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateContactType")
public interface UpdateContactTypeService extends RemoteService {
	UpdateResponse<ContactTypeInstance> updateContactType(ContactTypeInstance instance) throws IllegalArgumentException;
}
