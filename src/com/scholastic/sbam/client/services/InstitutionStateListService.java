package com.scholastic.sbam.client.services;

import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.InstitutionStateInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getInstitutionStates")
public interface InstitutionStateListService extends RemoteService {
	List<InstitutionStateInstance> getInstitutionStates(LoadConfig loadConfig) throws IllegalArgumentException;
}
