package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.InstitutionContactTuple;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("searchInstitutionContacts")
public interface InstitutionContactSearchService extends RemoteService {
	SynchronizedPagingLoadResult<InstitutionContactTuple> searchInstitutionContacts(LoadConfig config, long syncId) throws IllegalArgumentException;
}
