package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.TermTypeInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateTermType")
public interface UpdateTermTypeService extends RemoteService {
	UpdateResponse<TermTypeInstance> updateTermType(TermTypeInstance instance) throws IllegalArgumentException;
}
