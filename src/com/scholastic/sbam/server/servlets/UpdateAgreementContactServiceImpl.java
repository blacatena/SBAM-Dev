package com.scholastic.sbam.server.servlets;

import java.util.Date;
import java.util.List;

import com.scholastic.sbam.client.services.UpdateAgreementContactService;
import com.scholastic.sbam.server.database.codegen.AgreementContact;
import com.scholastic.sbam.server.database.codegen.AgreementContactId;
import com.scholastic.sbam.server.database.codegen.Contact;
import com.scholastic.sbam.server.database.codegen.InstitutionContact;
import com.scholastic.sbam.server.database.codegen.InstitutionContactId;
import com.scholastic.sbam.server.database.codegen.SiteContact;
import com.scholastic.sbam.server.database.codegen.SiteContactId;
import com.scholastic.sbam.server.database.objects.DbAgreementContact;
import com.scholastic.sbam.server.database.objects.DbContact;
import com.scholastic.sbam.server.database.objects.DbInstitutionContact;
import com.scholastic.sbam.server.database.objects.DbSiteContact;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.validation.AppAgreementContactValidator;
import com.scholastic.sbam.shared.objects.ContactInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.AgreementContactInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateAgreementContactServiceImpl extends AuthenticatedServiceServlet implements UpdateAgreementContactService {

	@Override
	public UpdateResponse<AgreementContactInstance> updateAgreementContact(AgreementContactInstance agreementContact) throws IllegalArgumentException {
		
		boolean newCreated				= false;
		
		String	messages				= null;
		
		Contact			 dbContact			= null;
		AgreementContact dbAgreementContact = null;
		
		authenticate("update agreement contact", SecurityManager.ROLE_MAINT);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Pre-edit/fix values
			validateInput(agreementContact);
			
			if (agreementContact.getContactId() > 0) {
				dbContact = DbContact.getByCode(agreementContact.getContactId());
			}
			
			if (dbContact == null) {	// create new contact
				dbContact = createContact(agreementContact);
				agreementContact.getContact().setContactId(dbContact.getContactId());
				agreementContact.getContact().setCreatedDatetime(dbContact.getCreatedDatetime());
			} else { // update existing contact
				updateContact(agreementContact, dbContact);
			}
			
			agreementContact.setContactId(dbContact.getContactId());
			DbContact.setDescriptions(agreementContact.getContact());
			
			//	Get existing, or create new
			if (agreementContact.getContactId() > 0) {
				dbAgreementContact = DbAgreementContact.getById(agreementContact.getAgreementId(), agreementContact.getContactId());
			}

			//	If none found, create new
			if (dbAgreementContact == null) {
				newCreated = true;
				dbAgreementContact = new AgreementContact();
				AgreementContactId id = new AgreementContactId();
				id.setAgreementId(agreementContact.getAgreementId());
				id.setContactId(agreementContact.getContactId());
				dbAgreementContact.setId(id);
				//	Set the create date/time and status
				dbAgreementContact.setCreatedDatetime(new Date());
			}

			//	Update values
			
			if (agreementContact.getStatus() != 0)
				dbAgreementContact.setStatus(agreementContact.getStatus());
			if (agreementContact.getRenewalContact() > AppConstants.STATUS_ANY_NONE)
				dbAgreementContact.setRenewalContact(agreementContact.getRenewalContact());
//			if (instance.getNote() != null)
//				dbAgreementContact.setNote(instance.getNote());
			
			//	Persist in database
			DbAgreementContact.persist(dbAgreementContact);
			
			//	Refresh when new row is created, to get assigned ID
			if (newCreated) {
			//	DbAgreementContact.refresh(dbAgreementContact);	// This may not be necessary, but just in case
			//	agreementContact.setContactId(dbAgreementContact.getId().getContactId());
				agreementContact.setCreatedDatetime(dbAgreementContact.getCreatedDatetime());
				DbAgreementContact.setDescriptions(agreementContact);
			}
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The agreement contact update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return new UpdateResponse<AgreementContactInstance>(agreementContact, messages);
	}
	
	protected void validateInput(AgreementContactInstance instance) throws IllegalArgumentException {
		AppAgreementContactValidator validator = new AppAgreementContactValidator();
		validator.setOriginal(instance);	//	This isn't really the original, but it's good enough, because it has the original ID
		testMessages(validator.validateAgreementContact(instance));
	}
	
	protected Contact createContact(AgreementContactInstance agreementContact) {
		Contact dbContact = new Contact();
		
		setDbContactFromInstance(agreementContact, dbContact);
		fixNulls(dbContact);
		
		DbContact.persist(dbContact);
		
		if (dbContact.getParentUcn() > 0) {
			removeInstitutionContacts(dbContact.getParentUcn(), dbContact.getContactId());
			createInstitutionContact(dbContact.getParentUcn(), dbContact.getContactId());
		}
		
		return dbContact;
	}
	
	protected void updateContact(AgreementContactInstance agreementContact, Contact dbContact) {
//		int originalParentUcn = dbContact.getParentUcn();
		
		setDbContactFromInstance(agreementContact, dbContact);
		fixNulls(dbContact);
		
		DbContact.persist(dbContact);
		
//		if (originalParentUcn > 0 && dbContact.getParentUcn() != originalParentUcn) {
//			removeInstitutionContact(originalParentUcn, dbContact.getContactId());
//		}
//		if (dbContact.getParentUcn() > 0 && dbContact.getParentUcn() != originalParentUcn) {
//			createInstitutionContact(dbContact.getParentUcn(), dbContact.getContactId());
//		}
		removeInstitutionContacts(dbContact.getParentUcn(), dbContact.getContactId());
		createInstitutionContact(dbContact.getParentUcn(), dbContact.getContactId());
	}
	
	protected void setDbContactFromInstance(AgreementContactInstance agreementContact, Contact dbContact) {
		ContactInstance source = agreementContact.getContact();
		
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
	
	protected void fixNulls(Contact dbContact) {
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
	
	/**
	 * Not used... removeInstitutionContact instead
	 * @param ucn
	 * @param contactId
	 */
	protected void removeSiteContact(int ucn, int contactId) {
		SiteContact siteContact = DbSiteContact.getById(ucn, contactId);
		if (siteContact != null) {
			siteContact.setStatus(AppConstants.STATUS_DELETED);
			DbSiteContact.persist(siteContact);
		}
	}
	
	/**
	 * Not used... createInstitutionContact instead
	 * @param ucn
	 * @param contactId
	 */
	protected void createSiteContact(int ucn, int contactId) {
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
	
	protected void removeInstitutionContact(int ucn, int contactId) {
		InstitutionContact siteContact = DbInstitutionContact.getById(ucn, contactId);
		if (siteContact != null) {
			siteContact.setStatus(AppConstants.STATUS_DELETED);
			DbInstitutionContact.persist(siteContact);
		}
	}
	
	protected void removeInstitutionContacts(int keepUcn, int contactId) {
		List<InstitutionContact> institutionContacts = DbInstitutionContact.findByContactId(contactId, AppConstants.STATUS_ANY_NONE, AppConstants.STATUS_ANY_NONE);
		for (InstitutionContact institutionContact : institutionContacts) {
			if (institutionContact != null && institutionContact.getId().getUcn() != keepUcn) {
				institutionContact.setStatus(AppConstants.STATUS_DELETED);
				DbInstitutionContact.persist(institutionContact);
			}
		}
	}
	
	protected void createInstitutionContact(int ucn, int contactId) {
		InstitutionContact institutionContact = DbInstitutionContact.getById(ucn, contactId);
		if (institutionContact == null) {
			institutionContact = new InstitutionContact();
			InstitutionContactId scid = new InstitutionContactId();
			scid.setContactId(contactId);
			scid.setUcn(ucn);
			institutionContact.setId(scid);
			institutionContact.setCreatedDatetime(new Date());
		}
		institutionContact.setStatus(AppConstants.STATUS_ACTIVE);
		DbInstitutionContact.persist(institutionContact);
	}
	
	protected void testMessages(List<String> messages) throws IllegalArgumentException {
		if (messages != null)
			for (String message: messages)
				testMessage(message);
	}
	
	protected void testMessage(String message) throws IllegalArgumentException {
		if (message != null && message.length() > 0)
			throw new IllegalArgumentException(message);
	}
	
	protected void silentRollback() {
		try {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.getSession().getTransaction().rollback();	
		} catch (Exception exc) { }
	}
}
