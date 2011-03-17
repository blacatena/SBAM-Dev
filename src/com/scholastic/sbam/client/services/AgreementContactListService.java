package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.AgreementContactInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getAgreementContacts")
public interface AgreementContactListService extends RemoteService {
	List<AgreementContactInstance> getAgreementContacts(int agreementId, char neStatus) throws IllegalArgumentException;
}
