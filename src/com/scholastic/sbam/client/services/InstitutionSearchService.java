package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.InstitutionInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getInstitutions")
public interface InstitutionSearchService extends RemoteService {
	PagingLoadResult<InstitutionInstance> getInstitutions(PagingLoadConfig loadConfig, String filter) throws IllegalArgumentException, ServiceNotReadyException;
}
