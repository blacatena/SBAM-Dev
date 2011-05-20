package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.AgreementContactGetService;
import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.AgreementContact;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.objects.DbAgreement;
import com.scholastic.sbam.server.database.objects.DbAgreementContact;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.objects.AgreementContactInstance;
import com.scholastic.sbam.shared.objects.AgreementContactTuple;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AgreementContactGetServiceImpl extends AgreementGetServiceBase implements AgreementContactGetService {

	@Override
	public AgreementContactTuple getAgreementContact(int agreementId, int contactId, boolean loadTerms, boolean allTerms) throws IllegalArgumentException {
		
		authenticate("get agreement contact", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		AgreementInstance		agreement = null;
		AgreementContactInstance	agreementContact = null;
		AgreementContactTuple		agreementContactTuple = null;
		try {
			Agreement		dbInstance = DbAgreement.getById(agreementId);
			AgreementContact	dbContactInstance = DbAgreementContact.getById(agreementId, contactId);
			if (dbInstance != null && dbContactInstance != null) {
				agreement = DbAgreement.getInstance(dbInstance);
				setDescriptions(agreement);
				agreementContact = DbAgreementContact.getInstance(dbContactInstance);
				setDescriptions(agreementContact);
				
				// Get the institution
				if (agreement.getBillUcn() > 0) {
					Institution dbInstitution = DbInstitution.getByCode(agreement.getBillUcn());
					agreement.setInstitution(DbInstitution.getInstance(dbInstitution));

					if (agreement.getInstitution() != null) {
						InstitutionCache.getSingleton().setDescriptions( agreement.getInstitution() );
					}
				}
				
				agreementContactTuple = new AgreementContactTuple(agreement, agreementContact);
				
				if (loadTerms)
					loadAgreementTerms(agreement, allTerms);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return agreementContactTuple;
	}
	
	public void setDescriptions(AgreementContactInstance contact) {
		DbAgreementContact.setDescriptions(contact);
	}
}
