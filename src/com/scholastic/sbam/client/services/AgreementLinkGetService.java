package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.AgreementLinkTuple;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getAgreementLink")
public interface AgreementLinkGetService extends RemoteService {
	AgreementLinkTuple getAgreementLink(int linkId, boolean loadAgreements, boolean allAgreements) throws IllegalArgumentException;
}
