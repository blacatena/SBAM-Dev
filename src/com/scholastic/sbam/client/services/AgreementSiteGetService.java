package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.AgreementSiteTuple;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getAgreementSite")
public interface AgreementSiteGetService extends RemoteService {
	AgreementSiteTuple getAgreementSite(int agreementId, int ucn, int ucnSuffix, String siteLocCode, boolean loadTerms, boolean allTerms) throws IllegalArgumentException;
}
