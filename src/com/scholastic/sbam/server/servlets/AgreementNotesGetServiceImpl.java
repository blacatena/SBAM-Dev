package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.AgreementNotesGetService;
import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.objects.DbAgreement;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.objects.AgreementNotesTuple;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * Get an agreement notes tuple.
 * 
 * THIS METHOD IS CURRENTLY INCOMPLETE.  It only gets the agreement (and terms, if requested), and nothing else.
 */
@SuppressWarnings("serial")
public class AgreementNotesGetServiceImpl extends AgreementGetServiceBase implements AgreementNotesGetService {

	@Override
	public AgreementNotesTuple getAgreementNotes(int agreementId, boolean loadTerms, boolean allTerms) throws IllegalArgumentException {
		
		authenticate("get agreement contact", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		AgreementInstance		agreement = null;
//		AgreementNotesInstance	agreementNotes = null;
		AgreementNotesTuple		agreementNotesTuple = null;
		try {
			Agreement		dbInstance = DbAgreement.getById(agreementId);
//			AgreementNotes	dbNotesInstance = DbAgreementNotes.getById(agreementId, contactId);
			if (dbInstance != null) {
				agreement = DbAgreement.getInstance(dbInstance);
				setDescriptions(agreement);
				
//				agreementNotes = DbAgreementNotes.getInstance(dbNotesInstance);
//				setDescriptions(agreementNotes);
				
				// Get the institution
				if (agreement.getBillUcn() > 0) {
					Institution dbInstitution = DbInstitution.getByCode(agreement.getBillUcn());
					agreement.setInstitution(DbInstitution.getInstance(dbInstitution));

					if (agreement.getInstitution() != null) {
						InstitutionCache.getSingleton().setDescriptions( agreement.getInstitution() );
					}
				}
				
				agreementNotesTuple = new AgreementNotesTuple(agreement);
				
				if (loadTerms)
					loadAgreementTerms(agreement, allTerms);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return agreementNotesTuple;
	}
}
