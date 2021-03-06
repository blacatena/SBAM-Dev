package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getCustomers")
public interface CustomerSearchService extends RemoteService {
	SynchronizedPagingLoadResult<InstitutionInstance> getCustomers(PagingLoadConfig loadConfig, String filter, boolean includeAgreementSummaries, long syncId) throws IllegalArgumentException, ServiceNotReadyException;
}
