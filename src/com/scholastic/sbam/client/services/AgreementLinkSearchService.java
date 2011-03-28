package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.AgreementLinkInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("searchAgreementLinks")
public interface AgreementLinkSearchService extends RemoteService {
	SynchronizedPagingLoadResult<AgreementLinkInstance> searchAgreementLinks(PagingLoadConfig loadConfig, String filter, long syncId) throws IllegalArgumentException, ServiceNotReadyException;
}
