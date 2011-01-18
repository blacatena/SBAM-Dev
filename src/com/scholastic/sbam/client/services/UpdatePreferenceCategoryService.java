package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.PreferenceCategoryInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updatePreferenceCategory")
public interface UpdatePreferenceCategoryService extends RemoteService {
	UpdateResponse<PreferenceCategoryInstance> updatePreferenceCategory(PreferenceCategoryInstance instance) throws IllegalArgumentException;
}
