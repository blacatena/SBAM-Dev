package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.AgreementSiteInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("searchAgreementSites")
public interface AgreementSiteSearchService extends RemoteService {
	SynchronizedPagingLoadResult<AgreementSiteInstance> searchAgreementSites(PagingLoadConfig loadConfig, int agreementId, String filter, char neStatus, long syncId) throws IllegalArgumentException;
}
