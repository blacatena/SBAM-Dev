package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.AgreementSiteTuple;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("searchAgreementsBySite")
public interface AgreementBySiteSearchService extends RemoteService {
	SynchronizedPagingLoadResult<AgreementSiteTuple> searchAgreementsBySite(LoadConfig config, long syncId) throws IllegalArgumentException;
}
