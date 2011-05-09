package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.SiteInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getSiteLocation")
public interface SiteLocationGetService extends RemoteService {
	SiteInstance getSiteLocation(int ucn, int ucnSuffix, String siteLocCode) throws IllegalArgumentException, ServiceNotReadyException;
}
