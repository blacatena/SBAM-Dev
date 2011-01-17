package com.scholastic.sbam.server.servlets;

import java.util.Date;
import java.util.List;

import com.scholastic.sbam.client.services.UpdateDeleteReasonService;
import com.scholastic.sbam.server.database.codegen.DeleteReason;
import com.scholastic.sbam.server.database.objects.DbDeleteReason;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.validation.AppDeleteReasonValidator;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.DeleteReasonInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateDeleteReasonServiceImpl extends AuthenticatedServiceServlet implements UpdateDeleteReasonService {

	@Override
	public UpdateResponse<DeleteReasonInstance> updateDeleteReason(DeleteReasonInstance instance) throws IllegalArgumentException {
		
		boolean newCreated				= false;
		
		String	messages				= null;
		
		DeleteReason dbInstance = null;
		
		@SuppressWarnings("unused")
		Authentication auth = authenticate("update delete reasons", SecurityManager.ROLE_CONFIG);	// May later be used for logging activity
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Pre-edit/fix values
			validateInput(instance);
			
			//	Get existing, or create new
			if (instance.getDeleteReasonCode() != null) {
				dbInstance = DbDeleteReason.getByCode(instance.getDeleteReasonCode());
			}

			//	If none found, create new
			if (dbInstance == null) {
				newCreated = true;
				dbInstance = new DeleteReason();
				//	Set the create date/time
				dbInstance.setCreatedDatetime(new Date());
			}

			//	Update values
			if (instance.getDeleteReasonCode() != null)
				dbInstance.setDeleteReasonCode(instance.getDeleteReasonCode());
			if (instance.getDescription() != null)
				dbInstance.setDescription(instance.getDescription());
			if (instance.getStatus() != 0 && instance.getStatus() != dbInstance.getStatus())
				dbInstance.setStatus(instance.getStatus());
			
			dbInstance.setStatus(instance.getStatus());
			
			//	Persist in database
			DbDeleteReason.persist(dbInstance);
			
			//	Refresh when new row is created, to get assigend ID
			if (newCreated) {
				DbDeleteReason.refresh(dbInstance);	// This may not be necessary, but just in case
			//	instance.setId(dbInstance.getId());	// Not autoincrement, so not needed
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
		
		return new UpdateResponse<DeleteReasonInstance>(instance, messages);
	}
	
	private void validateInput(DeleteReasonInstance instance) throws IllegalArgumentException {
//		testMessage(new AppUserNameValidator().validate(instance.getUserName()));
//	//	testMessage(new AppPasswordValidator().validate(instance.getPassword()));
//		testMessage(new EmailValidator().validate(instance.getEmail()));
//		testMessage(new AppRoleGroupValidator().validate(instance.getRoleGroupTitle()));
		AppDeleteReasonValidator validator = new AppDeleteReasonValidator();
		validator.setOriginal(instance);	//	This isn't really the original, but it's good enough, because it has the original ID
		testMessages(validator.validateDeleteReason(instance));
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
