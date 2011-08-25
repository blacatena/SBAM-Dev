package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.client.services.AgreementLinkGetService;
import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.AgreementLink;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.objects.DbAgreement;
import com.scholastic.sbam.server.database.objects.DbAgreementLink;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.server.fastSearch.InstitutionCache.InstitutionCacheConflict;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.objects.AgreementLinkInstance;
import com.scholastic.sbam.shared.objects.AgreementLinkTuple;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AgreementLinkGetServiceImpl extends AuthenticatedServiceServlet implements AgreementLinkGetService {

	@Override
	public AgreementLinkTuple getAgreementLink(int linkId, boolean loadAgreements, boolean allAgreements) throws IllegalArgumentException {
		
		authenticate("get agreement link", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		AgreementLinkTuple	agreementLinkTuple = null;
		try {
			AgreementLink	dbInstance = DbAgreementLink.getById(linkId);
			if (dbInstance != null) {
				AgreementLinkInstance agreementLinkInstance = DbAgreementLink.getInstance(dbInstance);
				setDescriptions(agreementLinkInstance);
				
				// Get the institution
				if (agreementLinkInstance.getUcn() > 0) {
					Institution dbInstitution = DbInstitution.getByCode(agreementLinkInstance.getUcn());
					agreementLinkInstance.setInstitution(DbInstitution.getInstance(dbInstitution));

					if (agreementLinkInstance.getInstitution() != null) {
						InstitutionCache.getSingleton().setDescriptions( agreementLinkInstance.getInstitution() );
					}
				}
				
				if (loadAgreements)
					agreementLinkTuple = new AgreementLinkTuple(agreementLinkInstance, getAgreements(agreementLinkInstance, allAgreements));
				else
					agreementLinkTuple = new AgreementLinkTuple(agreementLinkInstance, null);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return agreementLinkTuple;
	}
	
	public void setDescriptions(AgreementLinkInstance contact) {
		DbAgreementLink.setDescriptions(contact);
	}

	private void setDescriptions(AgreementInstance agreement) {
		DbAgreement.setDescriptions(agreement);
	}
	
	public List<AgreementInstance> getAgreements(AgreementLinkInstance agreementLinkInstance, boolean allAgreements) throws InstitutionCacheConflict {

		List<AgreementInstance> list = new ArrayList<AgreementInstance>();
		
		List<Agreement> agreements = DbAgreement.findFiltered(-1, -1, agreementLinkInstance.getLinkId(), null, null);
		
		for (Agreement dbInstance : agreements) {
			if (dbInstance != null) {
				AgreementInstance agreement = DbAgreement.getInstance(dbInstance);
				setDescriptions(agreement);
				
				// Get the institution
				if (agreement.getBillUcn() > 0) {
					Institution dbInstitution = DbInstitution.getByCode(agreement.getBillUcn());
					agreement.setInstitution(DbInstitution.getInstance(dbInstitution));

					if (agreement.getInstitution() != null) {
						InstitutionCache.getSingleton().setDescriptions( agreement.getInstitution() );
					}
				}
				
				list.add(agreement);
			}
		}
		
		return list;
	}
}
