package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.SearchResultInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("searchHelpText")
public interface HelpTextSearchService extends RemoteService {
	PagingLoadResult<SearchResultInstance> searchHelpText(PagingLoadConfig loadConfig, String filter) throws IllegalArgumentException, ServiceNotReadyException;
}
