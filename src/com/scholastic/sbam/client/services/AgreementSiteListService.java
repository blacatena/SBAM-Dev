package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.AgreementSiteInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getAgreementSites")
public interface AgreementSiteListService extends RemoteService {
	SynchronizedPagingLoadResult<AgreementSiteInstance> getAgreementSites(PagingLoadConfig config, int agreementId, char neStatus, long syncId) throws IllegalArgumentException;
}
