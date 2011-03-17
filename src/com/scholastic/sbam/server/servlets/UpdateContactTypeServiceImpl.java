package com.scholastic.sbam.server.servlets;

import java.util.Date;
import java.util.List;

import com.scholastic.sbam.client.services.UpdateContactTypeService;
import com.scholastic.sbam.server.database.codegen.ContactType;
import com.scholastic.sbam.server.database.objects.DbContactType;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.validation.AppContactTypeValidator;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.ContactTypeInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateContactTypeServiceImpl extends AuthenticatedServiceServlet implements UpdateContactTypeService {

	@Override
	public UpdateResponse<ContactTypeInstance> updateContactType(ContactTypeInstance instance) throws IllegalArgumentException {
		
		boolean newCreated				= false;
		
		String	messages				= null;
		
		ContactType dbInstance = null;
		
		@SuppressWarnings("unused")
		Authentication auth = authenticate("update contact types", SecurityManager.ROLE_CONFIG);	// May later be used for logging activity
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Pre-edit/fix values
			validateInput(instance);
			
			//	Get existing, or create new
			if (instance.getContactTypeCode() != null) {
				dbInstance = DbContactType.getByCode(instance.getContactTypeCode());
			}

			//	If none found, create new
			if (dbInstance == null) {
				newCreated = true;
				dbInstance = new ContactType();
				//	Set the create date/time
				dbInstance.setCreatedDatetime(new Date());
				dbInstance.setStatus('I');
			}

			//	Update values
			if (instance.getContactTypeCode() != null)
				dbInstance.setContactTypeCode(instance.getContactTypeCode());
			if (instance.getDescription() != null)
				dbInstance.setDescription(instance.getDescription());
			if (instance.getStatus() != 0 && instance.getStatus() != dbInstance.getStatus())
				dbInstance.setStatus(instance.getStatus());
			
			//	Persist in database
			DbContactType.persist(dbInstance);
			
			//	Refresh when new row is created, to get assigned ID
			if (newCreated) {
			//	DbContactType.refresh(dbInstance);	// This may not be necessary, but just in case
			//	instance.setId(dbInstance.getId());	// Not auto-increment, so not needed
				instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
			}
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return new UpdateResponse<ContactTypeInstance>(instance, messages);
	}
	
	private void validateInput(ContactTypeInstance instance) throws IllegalArgumentException {
		AppContactTypeValidator validator = new AppContactTypeValidator();
		validator.setOriginal(instance);	//	This isn't really the original, but it's good enough, because it has the original ID
		testMessages(validator.validateContactType(instance));
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
