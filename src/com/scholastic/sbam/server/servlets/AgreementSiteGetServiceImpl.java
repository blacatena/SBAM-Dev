package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.AgreementSiteGetService;
import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.AgreementSite;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.objects.DbAgreement;
import com.scholastic.sbam.server.database.objects.DbAgreementSite;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.objects.AgreementSiteInstance;
import com.scholastic.sbam.shared.objects.AgreementSiteTuple;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AgreementSiteGetServiceImpl extends AgreementGetServiceBase implements AgreementSiteGetService {

	@Override
	public AgreementSiteTuple getAgreementSite(int agreementId, int ucn, int ucnSuffix, String siteLocCode, boolean loadTerms, boolean allTerms) throws IllegalArgumentException {
		
		authenticate("get agreement term", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		AgreementInstance		agreement = null;
		AgreementSiteInstance	agreementSite = null;
		AgreementSiteTuple		agreementSiteTuple = null;
		try {
			Agreement		dbInstance = DbAgreement.getById(agreementId);
			AgreementSite	dbSiteInstance = DbAgreementSite.getById(agreementId, ucn, ucnSuffix, siteLocCode);
			if (dbInstance != null && dbSiteInstance != null) {
				agreement = DbAgreement.getInstance(dbInstance);
				setDescriptions(agreement);
				agreementSite = DbAgreementSite.getInstance(dbSiteInstance);
				setDescriptions(agreementSite);
				
				// Get the institution
				if (agreement.getBillUcn() > 0) {
					Institution dbInstitution = DbInstitution.getByCode(agreement.getBillUcn());
					agreement.setInstitution(DbInstitution.getInstance(dbInstitution));

					if (agreement.getInstitution() != null) {
						InstitutionCache.getSingleton().setDescriptions( agreement.getInstitution() );
					}
				}
				
				agreementSiteTuple = new AgreementSiteTuple(agreement, agreementSite);
				
				if (loadTerms)
					loadAgreementTerms(agreement, allTerms);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return agreementSiteTuple;
	}
	
	private void setDescriptions(AgreementSiteInstance agreementSite) {
		DbAgreementSite.setDescriptions(agreementSite);
	}
}
