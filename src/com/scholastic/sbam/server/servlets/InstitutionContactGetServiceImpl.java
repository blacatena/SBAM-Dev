package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.InstitutionContactGetService;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.InstitutionContact;
import com.scholastic.sbam.server.database.objects.DbAgreement;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.objects.DbInstitutionContact;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.server.fastSearch.InstitutionCache.InstitutionCacheConflict;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.InstitutionContactInstance;
import com.scholastic.sbam.shared.objects.InstitutionContactTuple;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class InstitutionContactGetServiceImpl extends AuthenticatedServiceServlet implements InstitutionContactGetService {

	@Override
	public InstitutionContactTuple getInstitutionContact(int ucn, int contactId, boolean includeAgreementSummaries) throws IllegalArgumentException {
		
		authenticate("get institution contact", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		InstitutionInstance		institution = null;
		InstitutionContactInstance	institutionContact = null;
		
		InstitutionContactTuple	institutionContactTuple = null;
		try {
			Institution		dbInstance = DbInstitution.getByCode(ucn);
			InstitutionContact	dbInstitutionContactInstance = DbInstitutionContact.getById(ucn, contactId);
			if (dbInstance != null && dbInstitutionContactInstance != null && dbInstitutionContactInstance != null) {
				institution = DbInstitution.getInstance(dbInstance);
				setDescriptions(institution);
				institutionContact = DbInstitutionContact.getInstance(dbInstitutionContactInstance);
				setDescriptions(institutionContact);
				
				institutionContactTuple = new InstitutionContactTuple(institution, institutionContact);
				
				if (includeAgreementSummaries)
					institutionContactTuple.getInstitution().setAgreementSummaryList(DbAgreement.findAllAgreementSummaries(institutionContactTuple.getInstitution().getUcn(), false, (char) 0, AppConstants.STATUS_DELETED));
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return institutionContactTuple;
	}
	
	public void setDescriptions(InstitutionInstance institution) throws InstitutionCacheConflict {
		institution.setTypeDescription(InstitutionCache.getSingleton().getInstitutionType(institution.getTypeCode()).getDescription());
		institution.setGroupDescription(InstitutionCache.getSingleton().getInstitutionGroup(institution.getGroupCode()).getDescription());
		institution.setPublicPrivateDescription(InstitutionCache.getSingleton().getInstitutionPubPriv(institution.getPublicPrivateCode()).getDescription());
	}
	
	public void setDescriptions(InstitutionContactInstance contact) {
//		DbInstitutionContact.setDescriptions(contact);
	}
}
