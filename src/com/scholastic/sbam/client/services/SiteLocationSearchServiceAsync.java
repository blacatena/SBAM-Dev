package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

public interface SiteLocationSearchServiceAsync {

	void searchSiteLocations(PagingLoadConfig loadConfig, int ucn, int ucnSuffix, String filter, long syncId, AsyncCallback<SynchronizedPagingLoadResult<SiteInstance>> callback);

}
