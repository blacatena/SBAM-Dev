package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.ContactSearchResultInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

public interface ContactSearchServiceAsync {

	void searchContacts(PagingLoadConfig loadConfig, int ucn, boolean searchInstitutions, String filter, long syncId, AsyncCallback<SynchronizedPagingLoadResult<ContactSearchResultInstance>> callback);

}
