package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateCommissionType")
public interface UpdateCommissionTypeService extends RemoteService {
	UpdateResponse<CommissionTypeInstance> updateCommissionType(CommissionTypeInstance instance) throws IllegalArgumentException;
}
