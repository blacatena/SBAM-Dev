package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.SiteContactTuple;

public interface SiteContactGetServiceAsync {

	void getSiteContact(int ucn, int ucnSuffix, String siteLocCode,  int contactId, AsyncCallback<SiteContactTuple> callback);

}
