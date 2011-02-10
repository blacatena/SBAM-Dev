package com.scholastic.sbam.server.servlets;

import java.util.Date;
import java.util.List;

import com.scholastic.sbam.client.services.UpdateWelcomeMessageService;
import com.scholastic.sbam.server.database.codegen.WelcomeMessage;
import com.scholastic.sbam.server.database.objects.DbWelcomeMessage;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.validation.AppWelcomeMessageValidator;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.WelcomeMessageInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateWelcomeMessageServiceImpl extends AuthenticatedServiceServlet implements UpdateWelcomeMessageService {

	@Override
	public UpdateResponse<WelcomeMessageInstance> updateWelcomeMessage(WelcomeMessageInstance instance) throws IllegalArgumentException {
		
		boolean newCreated				= false;
		
		String	messages				= null;
		
		WelcomeMessage dbInstance = null;
		
		@SuppressWarnings("unused")
		Authentication auth = authenticate("update welcome message", SecurityManager.ROLE_CONFIG);	// May later be used for logging activity
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Pre-edit/fix values
			validateInput(instance);
			
			//	Get existing, or create new
			if (instance.getId() >= 0 && !instance.isNewRecord()) {
				dbInstance = DbWelcomeMessage.getById(instance.getId());
			}

			//	If none found, create new
			if (dbInstance == null) {
				newCreated = true;
				dbInstance = new WelcomeMessage();
				//	Set the create date/time
				dbInstance.setPostDate(new Date());
				dbInstance.setStatus('I');
			}

			//	Update values
			if (instance.getTitle() != null)
				dbInstance.setTitle(instance.getTitle());
			if (instance.getContent() != null)
				dbInstance.setContent(instance.getContent());
			if (instance.getExpireDate() != null)
				dbInstance.setExpireDate(instance.getExpireDate());
			if (instance.getStatus() != 0 && instance.getStatus() != dbInstance.getStatus())
				dbInstance.setStatus(instance.getStatus());
			
			//	Persist in database
			DbWelcomeMessage.persist(dbInstance);
			
			//	Refresh when new row is created, to get assigned ID
			if (newCreated) {
			//	DbWelcomeMessage.refresh(dbInstance);	// This may not be necessary, but just in case
				instance.setId(dbInstance.getId());
				instance.setPostDate(dbInstance.getPostDate());
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
		
		return new UpdateResponse<WelcomeMessageInstance>(instance, messages, newCreated);
	}
	
	private void validateInput(WelcomeMessageInstance instance) throws IllegalArgumentException {
		AppWelcomeMessageValidator validator = new AppWelcomeMessageValidator();
		validator.setOriginal(instance);	//	This isn't really the original, but it's good enough, because it has the original ID
		testMessages(validator.validateWelcomeMessage(instance));
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
