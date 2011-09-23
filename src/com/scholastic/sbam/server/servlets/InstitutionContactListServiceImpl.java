package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.client.services.InstitutionContactListService;
import com.scholastic.sbam.server.database.codegen.InstitutionContact;
import com.scholastic.sbam.server.database.codegen.Contact;
import com.scholastic.sbam.server.database.objects.DbInstitutionContact;
import com.scholastic.sbam.server.database.objects.DbContact;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.InstitutionContactInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class InstitutionContactListServiceImpl extends AuthenticatedServiceServlet implements InstitutionContactListService {

	@Override
	public List<InstitutionContactInstance> getInstitutionContacts(int ucn, char neStatus) throws IllegalArgumentException {
		
		authenticate("get institution contacts", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<InstitutionContactInstance> list = new ArrayList<InstitutionContactInstance>();
		try {
			//	Find only undeleted contact types
			List<InstitutionContact> contactInstances = DbInstitutionContact.findByUcn(ucn, AppConstants.STATUS_ANY_NONE, neStatus);
			
			for (InstitutionContact contactInstance : contactInstances) {
				list.add(DbInstitutionContact.getInstance(contactInstance));
			}
			
			for (InstitutionContactInstance contact : list) {
				setDescriptions(contact);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return list;
	}
	

	
	private void setDescriptions(InstitutionContactInstance institutionContact) {
		if (institutionContact == null)
			return;
		
		if (institutionContact.getContactId() > 0) {
			Contact dbContact = DbContact.getByCode(institutionContact.getContactId());
			if (dbContact != null) {
				institutionContact.setContact( DbContact.getInstance(dbContact) );
				DbContact.setDescriptions(institutionContact.getContact());
			}
		}
	}
}
