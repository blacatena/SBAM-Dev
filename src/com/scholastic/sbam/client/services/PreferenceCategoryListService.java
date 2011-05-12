package com.scholastic.sbam.client.services;

import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.PreferenceCategoryInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getPreferenceCategories")
public interface PreferenceCategoryListService extends RemoteService {
	List<PreferenceCategoryInstance> getPreferenceCategories(LoadConfig loadConfig, boolean includePreferenceCodes) throws IllegalArgumentException;
}
