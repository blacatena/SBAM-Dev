package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.RemoteSetupUrlGetService;
import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.RemoteSetupUrl;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.objects.DbAgreement;
import com.scholastic.sbam.server.database.objects.DbRemoteSetupUrl;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.objects.DbSite;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.objects.RemoteSetupUrlInstance;
import com.scholastic.sbam.shared.objects.RemoteSetupUrlTuple;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class RemoteSetupUrlGetServiceImpl extends AgreementGetServiceBase implements RemoteSetupUrlGetService {

	@Override
	public RemoteSetupUrlTuple getRemoteSetupUrl(int agreementId, int ucn, int ucnSuffix, String siteLocCode, int urlId, boolean loadTerms, boolean allTerms) throws IllegalArgumentException {
		
		authenticate("get remote setup url", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		SiteInstance		site			=	null;
		AgreementInstance	agreement		= null;
		RemoteSetupUrlInstance	remoteSetupUrl		= null;
		RemoteSetupUrlTuple		remoteSetupUrlTuple	= null;
		try {
			
			RemoteSetupUrl	dbRemoteSetupUrl = DbRemoteSetupUrl.getById(agreementId, ucn, ucnSuffix, siteLocCode, urlId);
		
			if (dbRemoteSetupUrl != null) {
				
				remoteSetupUrl = DbRemoteSetupUrl.getInstance(dbRemoteSetupUrl);
				setDescriptions(remoteSetupUrl);
				
				if (agreementId > 0) {
					Agreement	dbAgreement = DbAgreement.getById(agreementId);
					if (dbAgreement != null) {
						agreement = DbAgreement.getInstance(dbAgreement);
						setDescriptions(agreement);
						
						// Get the institution
						if (agreement.getBillUcn() > 0) {
							Institution dbInstitution = DbInstitution.getByCode(agreement.getBillUcn());
							agreement.setInstitution(DbInstitution.getInstance(dbInstitution));
		
							if (agreement.getInstitution() != null) {
								InstitutionCache.getSingleton().setDescriptions( agreement.getInstitution() );
							}
						}
					}
						
					remoteSetupUrlTuple = new RemoteSetupUrlTuple(agreement, remoteSetupUrl);
					
					if (loadTerms)
						loadAgreementTerms(agreement, allTerms);
				} else if (ucn > 0) {
					Site	dbSite = DbSite.getById(ucn, ucnSuffix, siteLocCode);
					if (dbSite != null) {
						site = DbSite.getInstance(dbSite);
						setDescriptions(site);
					}
						
					remoteSetupUrlTuple = new RemoteSetupUrlTuple(agreement, site);
				}
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return remoteSetupUrlTuple;
	}
	
	public void setDescriptions(RemoteSetupUrlInstance remoteSetupUrl) {
		DbRemoteSetupUrl.setDescriptions(remoteSetupUrl);
	}
	
	public void setDescriptions(SiteInstance site) {
		DbSite.setDescriptions(site);
	}
}
