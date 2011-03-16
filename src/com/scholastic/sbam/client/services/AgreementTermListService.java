package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getAgreementTerms")
public interface AgreementTermListService extends RemoteService {
	List<AgreementTermInstance> getAgreementTerms(int agreementId, char neStatus) throws IllegalArgumentException;
}
