package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.RemoteSetupUrlInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getRemoteSetupUrls")
public interface RemoteSetupUrlListService extends RemoteService {
	List<RemoteSetupUrlInstance> getRemoteSetupUrls(int agreementId, int ucn, int ucnSuffix, String siteLocCode, char neStatus) throws IllegalArgumentException;
}
