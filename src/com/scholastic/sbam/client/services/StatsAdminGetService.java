package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.StatsAdminInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getStatsAdmin")
public interface StatsAdminGetService extends RemoteService {
	StatsAdminInstance getStatsAdmin(int ucn) throws IllegalArgumentException, ServiceNotReadyException;
}
