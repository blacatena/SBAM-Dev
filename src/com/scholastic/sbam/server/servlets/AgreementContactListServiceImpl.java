package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.client.services.AgreementContactListService;
import com.scholastic.sbam.server.database.codegen.AgreementContact;
import com.scholastic.sbam.server.database.codegen.Contact;
import com.scholastic.sbam.server.database.objects.DbAgreementContact;
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
		
		authenticate("get agreement contacts", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<AgreementContactInstance> list = new ArrayList<AgreementContactInstance>();
		try {
			//	Find only undeleted contacts
			List<AgreementContact> contacts = DbAgreementContact.findByAgreementId(agreementId, AppConstants.STATUS_ANY_NONE, neStatus);
			
			for (AgreementContact contact : contacts) {
				AgreementContactInstance contactInstance = DbAgreementContact.getInstance(contact);
				setDescriptions(contactInstance);
				if	(contactInstance.getContact() != null && contactInstance.getContact().getStatus() != neStatus)
					list.add(contactInstance);
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
				DbContact.setDescriptions(agreementContact.getContact());
			}
		}
	}
}
