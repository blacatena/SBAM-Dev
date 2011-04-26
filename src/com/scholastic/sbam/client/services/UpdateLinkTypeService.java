package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.LinkTypeInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateLinkType")
public interface UpdateLinkTypeService extends RemoteService {
	UpdateResponse<LinkTypeInstance> updateLinkType(LinkTypeInstance instance) throws IllegalArgumentException;
}
