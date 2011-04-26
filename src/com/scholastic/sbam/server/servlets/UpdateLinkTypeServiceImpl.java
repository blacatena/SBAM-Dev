package com.scholastic.sbam.server.servlets;

import java.util.Date;
import java.util.List;

import com.scholastic.sbam.client.services.UpdateLinkTypeService;
import com.scholastic.sbam.server.database.codegen.LinkType;
import com.scholastic.sbam.server.database.objects.DbLinkType;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.validation.AppLinkTypeValidator;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.LinkTypeInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateLinkTypeServiceImpl extends AuthenticatedServiceServlet implements UpdateLinkTypeService {

	@Override
	public UpdateResponse<LinkTypeInstance> updateLinkType(LinkTypeInstance instance) throws IllegalArgumentException {
		
		boolean newCreated				= false;
		
		String	messages				= null;
		
		LinkType dbInstance = null;
		
		@SuppressWarnings("unused")
		Authentication auth = authenticate("update link types", SecurityManager.ROLE_CONFIG);	// May later be used for logging activity
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Pre-edit/fix values
			validateInput(instance);
			
			//	Get existing, or create new
			if (instance.getLinkTypeCode() != null) {
				dbInstance = DbLinkType.getByCode(instance.getLinkTypeCode());
			}

			//	If none found, create new
			if (dbInstance == null) {
				newCreated = true;
				dbInstance = new LinkType();
				//	Set the create date/time
				dbInstance.setCreatedDatetime(new Date());
				dbInstance.setStatus('I');
			}

			//	Update values
			if (instance.getLinkTypeCode() != null)
				dbInstance.setLinkTypeCode(instance.getLinkTypeCode());
			if (instance.getDescription() != null)
				dbInstance.setDescription(instance.getDescription());
			if (instance.getStatus() != 0 && instance.getStatus() != dbInstance.getStatus())
				dbInstance.setStatus(instance.getStatus());
			
			//	Persist in database
			DbLinkType.persist(dbInstance);
			
			//	Refresh when new row is created, to get assigned ID
			if (newCreated) {
			//	DbLinkType.refresh(dbInstance);	// This may not be necessary, but just in case
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
		
		return new UpdateResponse<LinkTypeInstance>(instance, messages);
	}
	
	private void validateInput(LinkTypeInstance instance) throws IllegalArgumentException {
		AppLinkTypeValidator validator = new AppLinkTypeValidator();
		validator.setOriginal(instance);	//	This isn't really the original, but it's good enough, because it has the original ID
		testMessages(validator.validateLinkType(instance));
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
