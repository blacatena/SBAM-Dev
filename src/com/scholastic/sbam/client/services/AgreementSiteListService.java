package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.AgreementSiteInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getAgreementSites")
public interface AgreementSiteListService extends RemoteService {
	List<AgreementSiteInstance> getAgreementSites(int agreementId, char neStatus) throws IllegalArgumentException;
}
