package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.InstitutionContactInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getInstitutionContacts")
public interface InstitutionContactListService extends RemoteService {
	List<InstitutionContactInstance> getInstitutionContacts(int ucn, char neStatus) throws IllegalArgumentException;
}
