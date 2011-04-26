package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("searchAgreementSiteInstitutions")
public interface AgreementSiteInstitutionSearchService extends RemoteService {
	SynchronizedPagingLoadResult<InstitutionInstance> searchAgreementSiteInstitutions(PagingLoadConfig loadConfig, int agreementId, String filter, char neStatus, long syncId) throws IllegalArgumentException;
}
