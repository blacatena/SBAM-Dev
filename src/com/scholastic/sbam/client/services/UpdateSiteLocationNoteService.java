package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.SiteInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateSiteLocationNote")
public interface UpdateSiteLocationNoteService extends RemoteService {
	UpdateResponse<SiteInstance> updateSiteLocationNote(SiteInstance instance) throws IllegalArgumentException;
}
