package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.SiteContactTuple;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getSiteContact")
public interface SiteContactGetService extends RemoteService {
	SiteContactTuple getSiteContact(int ucn, int ucnSuffix, String siteLocCode,  int contactId) throws IllegalArgumentException;
}
