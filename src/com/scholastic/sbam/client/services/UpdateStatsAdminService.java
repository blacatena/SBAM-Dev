package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.StatsAdminInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateStatsAdmin")
public interface UpdateStatsAdminService extends RemoteService {
	UpdateResponse<StatsAdminInstance> updateStatsAdmin(StatsAdminInstance instance) throws IllegalArgumentException;
}
