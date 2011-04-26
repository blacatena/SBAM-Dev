package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("searchSiteLocations")
public interface SiteLocationSearchService extends RemoteService {
	SynchronizedPagingLoadResult<SiteInstance> searchSiteLocations(PagingLoadConfig loadConfig, int agreementId, int ucn, int ucnSuffix, String filter, long syncId) throws IllegalArgumentException, ServiceNotReadyException;
}
