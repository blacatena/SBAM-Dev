package com.scholastic.sbam.server.servlets;

import java.util.Date;
import java.util.List;

import com.scholastic.sbam.client.services.UpdateInstitutionContactService;
import com.scholastic.sbam.server.database.codegen.InstitutionContact;
import com.scholastic.sbam.server.database.codegen.InstitutionContactId;
import com.scholastic.sbam.server.database.codegen.Contact;
import com.scholastic.sbam.server.database.codegen.SiteContact;
import com.scholastic.sbam.server.database.codegen.SiteContactId;
import com.scholastic.sbam.server.database.objects.DbInstitutionContact;
import com.scholastic.sbam.server.database.objects.DbContact;
import com.scholastic.sbam.server.database.objects.DbSiteContact;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.validation.AppInstitutionContactValidator;
import com.scholastic.sbam.shared.objects.ContactInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.InstitutionContactInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateInstitutionContactServiceImpl extends AuthenticatedServiceServlet implements UpdateInstitutionContactService {

	@Override
	public UpdateResponse<InstitutionContactInstance> updateInstitutionContact(InstitutionContactInstance institutionContact) throws IllegalArgumentException {
		
		boolean newCreated				= false;
		
		String	messages				= null;
		
		Contact			 dbContact			= null;
		InstitutionContact dbInstitutionContact = null;
		
		authenticate("update institution contact", SecurityManager.ROLE_MAINT);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Pre-edit/fix values
			validateInput(institutionContact);
			
			if (institutionContact.getContactId() > 0) {
				dbContact = DbContact.getByCode(institutionContact.getContactId());
			}
			
			if (dbContact == null) {	// create new contact
				dbContact = createContact(institutionContact);
				institutionContact.getContact().setContactId(dbContact.getContactId());
				institutionContact.getContact().setCreatedDatetime(dbContact.getCreatedDatetime());
			} else { // update existing contact
				updateContact(institutionContact, dbContact);
			}
			
			institutionContact.setContactId(dbContact.getContactId());
			DbContact.setDescriptions(institutionContact.getContact());
			
			//	Get existing, or create new
			if (institutionContact.getContactId() > 0) {
				dbInstitutionContact = DbInstitutionContact.getById(institutionContact.getUcn(), institutionContact.getContactId());
			}

			//	If none found, create new
			if (dbInstitutionContact == null) {
				newCreated = true;
				dbInstitutionContact = new InstitutionContact();
				InstitutionContactId id = new InstitutionContactId();
				id.setUcn(institutionContact.getUcn());
				id.setContactId(institutionContact.getContactId());
				dbInstitutionContact.setId(id);
				//	Set the create date/time and status
				dbInstitutionContact.setCreatedDatetime(new Date());
			}

			//	Update values
			
			if (institutionContact.getStatus() != 0)
				dbInstitutionContact.setStatus(institutionContact.getStatus());
//			if (instance.getNote() != null)
//				dbInstitutionContact.setNote(instance.getNote());
			
			//	Persist in database
			DbInstitutionContact.persist(dbInstitutionContact);
			
			//	Refresh when new row is created, to get assigned ID
			if (newCreated) {
			//	DbInstitutionContact.refresh(dbInstitutionContact);	// This may not be necessary, but just in case
			//	institutionContact.setContactId(dbInstitutionContact.getId().getContactId());
				institutionContact.setCreatedDatetime(dbInstitutionContact.getCreatedDatetime());
				DbInstitutionContact.setDescriptions(institutionContact);
			}
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The institution contact update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return new UpdateResponse<InstitutionContactInstance>(institutionContact, messages);
	}
	
	private void validateInput(InstitutionContactInstance instance) throws IllegalArgumentException {
		AppInstitutionContactValidator validator = new AppInstitutionContactValidator();
		validator.setOriginal(instance);	//	This isn't really the original, but it's good enough, because it has the original ID
		testMessages(validator.validateInstitutionContact(instance));
	}
	
	private Contact createContact(InstitutionContactInstance institutionContact) {
		Contact dbContact = new Contact();
		
		setDbContactFromInstance(institutionContact, dbContact);
		fixNulls(dbContact);
		
		DbContact.persist(dbContact);
		
		if (dbContact.getParentUcn() > 0) {
			createSiteContact(dbContact.getParentUcn(), dbContact.getContactId());
		}
		
		return dbContact;
	}
	
	private void updateContact(InstitutionContactInstance institutionContact, Contact dbContact) {
		int originalParentUcn = dbContact.getParentUcn();
		
		setDbContactFromInstance(institutionContact, dbContact);
		fixNulls(dbContact);
		
		DbContact.persist(dbContact);
		
		if (originalParentUcn > 0 && dbContact.getParentUcn() != originalParentUcn) {
			removeSiteContact(originalParentUcn, dbContact.getContactId());
		}
		if (dbContact.getParentUcn() > 0 && dbContact.getParentUcn() != originalParentUcn) {
			createSiteContact(dbContact.getParentUcn(), dbContact.getContactId());
		}
	}
	
	private void setDbContactFromInstance(InstitutionContactInstance institutionContact, Contact dbContact) {
		ContactInstance source = institutionContact.getContact();
		
		if (source.getParentUcn() > 0)
			dbContact.setParentUcn(source.getParentUcn());
		if (source.getFullName() != null)
			dbContact.setFullName(source.getFullName());
		if (source.getInstitution() != null)
			dbContact.setParentUcn(source.getInstitution().getUcn());
		if (source.getContactTypeCode() != null)
			dbContact.setContactTypeCode(source.getContactTypeCode());
		if (source.getTitle() != null)
			dbContact.setTitle(source.getTitle());
		if (source.getAdditionalInfo() != null)
			dbContact.setAdditionalInfo(source.getAdditionalInfo());
		if (source.getAddress1() != null)
			dbContact.setAddress1(source.getAddress1());
		if (source.getAddress2() != null)
			dbContact.setAddress2(source.getAddress2());
		if (source.getAddress3() != null)
			dbContact.setAddress3(source.getAddress3());
		if (source.getCity() != null)
			dbContact.setCity(source.getCity());
		if (source.getState() != null)
			dbContact.setState(source.getState());
		if (source.getZip() != null)
			dbContact.setZip(source.getZip());
		if (source.getCountry() != null)
			dbContact.setCountry(source.getCountry());
		if (source.getPhone() != null)
			dbContact.setPhone(source.getPhone());
		if (source.getPhone2() != null)
			dbContact.setPhone2(source.getPhone2());
		if (source.getFax() != null)
			dbContact.setFax(source.getFax());
		if (source.geteMail() != null)
			dbContact.setEMail(source.geteMail());
		if (source.geteMail2() != null)
			dbContact.setEMail2(source.geteMail2());
		if (source.getNote() != null)
			dbContact.setNote(source.getNote());
		if (source.getCreatedDatetime() != null)
			dbContact.setCreatedDatetime(source.getCreatedDatetime());
		if (source.getStatus() != AppConstants.STATUS_ANY_NONE)
			dbContact.setStatus(source.getStatus());		
	}
	
	private void fixNulls(Contact dbContact) {
		if (dbContact.getFullName() == null)
			dbContact.setFullName("");
		if (dbContact.getContactTypeCode() == null)
			dbContact.setContactTypeCode("");
		if (dbContact.getTitle() == null)
			dbContact.setTitle("");
		if (dbContact.getAdditionalInfo() == null)
			dbContact.setAdditionalInfo("");
		if (dbContact.getAddress1() == null)
			dbContact.setAddress1("");
		if (dbContact.getAddress2() == null)
			dbContact.setAddress2("");
		if (dbContact.getAddress3() == null)
			dbContact.setAddress3("");
		if (dbContact.getCity() == null)
			dbContact.setCity("");
		if (dbContact.getState() == null)
			dbContact.setState("");
		if (dbContact.getZip() == null)
			dbContact.setZip("");
		if (dbContact.getCountry() == null)
			dbContact.setCountry("");
		if (dbContact.getPhone() == null)
			dbContact.setPhone("");
		if (dbContact.getPhone2() == null)
			dbContact.setPhone2("");
		if (dbContact.getFax() == null)
			dbContact.setFax("");
		if (dbContact.getEMail() == null)
			dbContact.setEMail("");
		if (dbContact.getEMail2() == null)
			dbContact.setEMail2("");
		if (dbContact.getNote() == null)
			dbContact.setNote("");
		if (dbContact.getCreatedDatetime() == null)
			dbContact.setCreatedDatetime(new Date());
		if (dbContact.getStatus() == AppConstants.STATUS_ANY_NONE)
			dbContact.setStatus(AppConstants.STATUS_ACTIVE);
	}
	
	private void removeSiteContact(int ucn, int contactId) {
		SiteContact siteContact = DbSiteContact.getById(ucn, contactId);
		if (siteContact != null) {
			siteContact.setStatus(AppConstants.STATUS_DELETED);
			DbSiteContact.persist(siteContact);
		}
	}
	
	private void createSiteContact(int ucn, int contactId) {
		SiteContact siteContact = DbSiteContact.getById(ucn, contactId);
		if (siteContact == null) {
			siteContact = new SiteContact();
			SiteContactId scid = new SiteContactId();
			scid.setContactId(contactId);
			scid.setUcn(ucn);
			siteContact.setId(scid);
			siteContact.setCreatedDatetime(new Date());
		}
		siteContact.setStatus(AppConstants.STATUS_ACTIVE);
		DbSiteContact.persist(siteContact);
	}
	
	private void testMessages(List<String> messages) throws IllegalArgumentException {
		if (messages != null)
			for (String message: messages)
				testMessage(message);
	}
	
	private void testMessage(String message) throws IllegalArgumentException {
		if (message != null && message.length() > 0)
			throw new IllegalArgumentException(message);
	}
	
	private void silentRollback() {
		try {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.getSession().getTransaction().rollback();	
		} catch (Exception exc) { }
	}
}
