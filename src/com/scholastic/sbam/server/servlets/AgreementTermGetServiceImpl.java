package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.AgreementTermGetService;
import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.AgreementTerm;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.objects.DbAgreement;
import com.scholastic.sbam.server.database.objects.DbAgreementTerm;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;
import com.scholastic.sbam.shared.objects.AgreementTermTuple;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AgreementTermGetServiceImpl extends AgreementGetServiceBase implements AgreementTermGetService {

	@Override
	public AgreementTermTuple getAgreementTerm(int agreementId, int termId, boolean loadTerms, boolean allTerms) throws IllegalArgumentException {
		
		authenticate("get agreement term", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		AgreementInstance		agreement = null;
		AgreementTermInstance	agreementTerm = null;
		AgreementTermTuple		agreementTermTuple = null;
		try {
			Agreement		dbInstance = DbAgreement.getById(agreementId);
			AgreementTerm	dbTermInstance = DbAgreementTerm.getById(agreementId, termId);
			if (dbInstance != null && dbTermInstance != null) {
				agreement = DbAgreement.getInstance(dbInstance);
				setDescriptions(agreement);
				agreementTerm = DbAgreementTerm.getInstance(dbTermInstance);
				setDescriptions(agreementTerm);
				
				// Get the institution
				if (agreement.getBillUcn() > 0) {
					Institution dbInstitution = DbInstitution.getByCode(agreement.getBillUcn());
					agreement.setInstitution(DbInstitution.getInstance(dbInstitution));

					if (agreement.getInstitution() != null) {
						InstitutionCache.getSingleton().setDescriptions( agreement.getInstitution() );
					}
				}
				
				agreementTermTuple = new AgreementTermTuple(agreement, agreementTerm);
				
				if (loadTerms)
					loadAgreementTerms(agreement, allTerms);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return agreementTermTuple;
	}
}
