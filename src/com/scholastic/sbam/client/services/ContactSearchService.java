package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.ContactSearchResultInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("searchContacts")
public interface ContactSearchService extends RemoteService {
	SynchronizedPagingLoadResult<ContactSearchResultInstance> searchContacts(PagingLoadConfig loadConfig, int ucn, boolean searchInstitutions, String filter, long syncId) throws IllegalArgumentException, ServiceNotReadyException;
}
