package com.scholastic.sbam.client.services;

import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.CacheStatusInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("listCacheStatus")
public interface CacheStatusListService extends RemoteService {
	List<CacheStatusInstance> listCacheStatus(LoadConfig loadConfig) throws IllegalArgumentException, ServiceNotReadyException;
}
