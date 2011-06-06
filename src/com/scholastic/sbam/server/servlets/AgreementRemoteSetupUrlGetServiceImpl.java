package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.AgreementRemoteSetupUrlGetService;
import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.RemoteSetupUrl;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.objects.DbAgreement;
import com.scholastic.sbam.server.database.objects.DbRemoteSetupUrl;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.objects.RemoteSetupUrlInstance;
import com.scholastic.sbam.shared.objects.AgreementRemoteSetupUrlTuple;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AgreementRemoteSetupUrlGetServiceImpl extends AgreementGetServiceBase implements AgreementRemoteSetupUrlGetService {

	@Override
	public AgreementRemoteSetupUrlTuple getAgreementRemoteSetupUrl(int agreementId, int urlId, boolean loadTerms, boolean allTerms) throws IllegalArgumentException {
		
		authenticate("get agreement remote setup url", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		AgreementInstance				agreement = null;
		RemoteSetupUrlInstance			remoteSetupUrl = null;
		AgreementRemoteSetupUrlTuple	agreementRemoteSetupUrlTuple = null;
		try {
			Agreement		dbInstance = DbAgreement.getById(agreementId);
			RemoteSetupUrl	dbRemoteSetupUrlInstance = DbRemoteSetupUrl.getById(agreementId, -1, -1, null, urlId);
			if (dbInstance != null && dbRemoteSetupUrlInstance != null) {
				agreement = DbAgreement.getInstance(dbInstance);
				setDescriptions(agreement);
				remoteSetupUrl = DbRemoteSetupUrl.getInstance(dbRemoteSetupUrlInstance);
				setDescriptions(remoteSetupUrl);
				
				// Get the institution
				if (agreement.getBillUcn() > 0) {
					Institution dbInstitution = DbInstitution.getByCode(agreement.getBillUcn());
					agreement.setInstitution(DbInstitution.getInstance(dbInstitution));

					if (agreement.getInstitution() != null) {
						InstitutionCache.getSingleton().setDescriptions( agreement.getInstitution() );
					}
				}
				
				agreementRemoteSetupUrlTuple = new AgreementRemoteSetupUrlTuple(agreement, remoteSetupUrl);
				
				if (loadTerms)
					loadAgreementTerms(agreement, allTerms);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return agreementRemoteSetupUrlTuple;
	}
	
	public void setDescriptions(RemoteSetupUrlInstance contact) {
		DbRemoteSetupUrl.setDescriptions(contact);
	}
}
