package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.client.services.AgreementContactListService;
import com.scholastic.sbam.server.database.codegen.AgreementContact;
import com.scholastic.sbam.server.database.codegen.ContactType;
import com.scholastic.sbam.server.database.codegen.Contact;
import com.scholastic.sbam.server.database.objects.DbAgreementContact;
import com.scholastic.sbam.server.database.objects.DbContactType;
import com.scholastic.sbam.server.database.objects.DbContact;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.AgreementContactInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AgreementContactListServiceImpl extends AuthenticatedServiceServlet implements AgreementContactListService {

	@Override
	public List<AgreementContactInstance> getAgreementContacts(int agreementId, char neStatus) throws IllegalArgumentException {
		
		authenticate("get agreement sites", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<AgreementContactInstance> list = new ArrayList<AgreementContactInstance>();
		try {
			//	Find only undeleted site types
			List<AgreementContact> siteInstances = DbAgreementContact.findByAgreementId(agreementId, AppConstants.STATUS_ANY_NONE, neStatus);
			
			for (AgreementContact siteInstance : siteInstances) {
				list.add(DbAgreementContact.getInstance(siteInstance));
			}
			
			for (AgreementContactInstance site : list) {
				setDescriptions(site);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return list;
	}
	

	
	private void setDescriptions(AgreementContactInstance agreementContact) {
		if (agreementContact == null)
			return;
		
		if (agreementContact.getContactId() > 0) {
			Contact dbContact = DbContact.getByCode(agreementContact.getContactId());
			if (dbContact != null) {
				agreementContact.setContact( DbContact.getInstance(dbContact) );
			
				if (dbContact.getContactTypeCode() != null) {
					ContactType cType = DbContactType.getByCode(dbContact.getContactTypeCode());
					if (cType != null)
						agreementContact.getContact().setContactTypeDescription(cType.getDescription());
				}
			}
		}
	}
}
