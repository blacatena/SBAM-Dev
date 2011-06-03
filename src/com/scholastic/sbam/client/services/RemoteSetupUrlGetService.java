package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.RemoteSetupUrlTuple;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getRemoteSetupUrl")
public interface RemoteSetupUrlGetService extends RemoteService {
	RemoteSetupUrlTuple getRemoteSetupUrl(int agreementId, int ucn, int ucnSuffix, String siteLocCode, int urlId, boolean loadTerms, boolean allTerms) throws IllegalArgumentException;
}
