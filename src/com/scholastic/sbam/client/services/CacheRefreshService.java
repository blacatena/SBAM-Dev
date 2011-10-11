package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.CacheStatusInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("refreshCache")
public interface CacheRefreshService extends RemoteService {
	CacheStatusInstance refreshCache(String cacheKey) throws IllegalArgumentException, ServiceNotReadyException;
}
