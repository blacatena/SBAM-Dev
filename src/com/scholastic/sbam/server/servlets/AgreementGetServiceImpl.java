package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.AgreementGetService;
import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.objects.DbAgreement;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AgreementGetServiceImpl extends AgreementGetServiceBase implements AgreementGetService {

	@Override
	public AgreementInstance getAgreement(int agreementId, boolean allTerms) throws IllegalArgumentException {
		
		authenticate("get agreement", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		AgreementInstance agreement = null;
		try {
			Agreement dbInstance = DbAgreement.getById(agreementId);
			if (dbInstance != null) {
				agreement = DbAgreement.getInstance(dbInstance);
				setDescriptions(agreement);
				
				// Get the institution
				if (agreement.getBillUcn() > 0) {
					Institution dbInstitution = DbInstitution.getByCode(agreement.getBillUcn());
					agreement.setInstitution(DbInstitution.getInstance(dbInstitution));

					if (agreement.getInstitution() != null) {
						InstitutionCache.getSingleton().setDescriptions( agreement.getInstitution() );
					}
				}
				
				loadAgreementTerms(agreement, allTerms);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return agreement;
	}
}
