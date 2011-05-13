package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("searchAgreements")
public interface AgreementSearchService extends RemoteService {
	SynchronizedPagingLoadResult<AgreementInstance> searchAgreements(LoadConfig config, AgreementInstance sampleInstance, boolean currentTerms, long syncId) throws IllegalArgumentException;
}
