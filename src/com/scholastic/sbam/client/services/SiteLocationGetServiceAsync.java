package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.SiteInstance;

public interface SiteLocationGetServiceAsync {

	void getSiteLocation(int ucn, int ucnSuffix, String siteLocCode, AsyncCallback<SiteInstance> callback);

}
