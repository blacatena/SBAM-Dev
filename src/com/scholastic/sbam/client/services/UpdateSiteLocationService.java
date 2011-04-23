package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.SiteInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateSiteLocation")
public interface UpdateSiteLocationService extends RemoteService {
	UpdateResponse<SiteInstance> updateSiteLocation(SiteInstance instance) throws IllegalArgumentException;
}
