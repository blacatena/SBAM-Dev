package com.scholastic.sbam.client.services;

import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.ServiceInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getServices")
public interface ServiceListService extends RemoteService {
	List<ServiceInstance> getServices(LoadConfig loadConfig) throws IllegalArgumentException;
}
