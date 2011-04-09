package com.scholastic.sbam.server.servlets;

import java.util.Date;
import java.util.List;

import com.scholastic.sbam.client.services.UpdateAgreementContactService;
import com.scholastic.sbam.server.database.codegen.AgreementContact;
import com.scholastic.sbam.server.database.codegen.AgreementContactId;
import com.scholastic.sbam.server.database.codegen.Contact;
import com.scholastic.sbam.server.database.objects.DbAgreementContact;
import com.scholastic.sbam.server.database.objects.DbContact;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.validation.AppAgreementContactValidator;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.AgreementContactInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateAgreementContactServiceImpl extends AuthenticatedServiceServlet implements UpdateAgreementContactService {

	@Override
	public UpdateResponse<AgreementContactInstance> updateAgreementContact(AgreementContactInstance instance) throws IllegalArgumentException {
		
		boolean newCreated				= false;
		
		String	messages				= null;
		
		Contact			 dbContact			= null;
		AgreementContact dbAgreementContact = null;
		
		authenticate("update agreement contact", SecurityManager.ROLE_MAINT);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Pre-edit/fix values
			validateInput(instance);
			
			if (instance.getContactId() > 0) {
				dbContact = DbContact.getByCode(instance.getContactId());
			} else {
				dbContact = createContact(instance);
			}
			
			//	Get existing, or create new
			if (instance.getContactId() > 0) {
				dbAgreementContact = DbAgreementContact.getById(instance.getAgreementId(), instance.getContactId());
			}

			//	If none found, create new
			if (dbAgreementContact == null) {
				newCreated = true;
				dbAgreementContact = new AgreementContact();
				AgreementContactId id = new AgreementContactId();
				id.setAgreementId(instance.getAgreementId());
				id.setContactId(instance.getContactId());
				dbAgreementContact.setId(id);
				//	Set the create date/time and status
				dbAgreementContact.setCreatedDatetime(new Date());
			}

			//	Update values
			
			if (instance.getStatus() != 0)
				dbAgreementContact.setStatus(instance.getStatus());
//			if (instance.getNote() != null)
//				dbAgreementContact.setNote(instance.getNote());

			
				
			//	Fix any nulls
//			if (dbAgreementContact.getNote() == null)
//				dbAgreementContact.setNote("");
			
			//	Persist in database
			DbAgreementContact.persist(dbAgreementContact);
			
			//	Refresh when new row is created, to get assigned ID
			if (newCreated) {
			//	DbAgreementContact.refresh(dbAgreementContact);	// This may not be necessary, but just in case
				instance.setContactId(dbAgreementContact.getId().getContactId());
				instance.setCreatedDatetime(dbAgreementContact.getCreatedDatetime());
				DbAgreementContact.setDescriptions(instance);
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
		
		return new UpdateResponse<AgreementContactInstance>(instance, messages);
	}
	
	private void validateInput(AgreementContactInstance instance) throws IllegalArgumentException {
		AppAgreementContactValidator validator = new AppAgreementContactValidator();
		validator.setOriginal(instance);	//	This isn't really the original, but it's good enough, because it has the original ID
		testMessages(validator.validateAgreementContact(instance));
	}
	
	private Contact createContact(AgreementContactInstance agreementContact) {
		return null;
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
