package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.InstitutionInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getInstitution")
public interface InstitutionGetService extends RemoteService {
	InstitutionInstance getInstitution(int ucn) throws IllegalArgumentException, ServiceNotReadyException;
}
