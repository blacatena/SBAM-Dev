package com.scholastic.sbam.server.servlets;

import java.util.Date;
import java.util.List;

import com.scholastic.sbam.client.services.UpdateCancelReasonService;
import com.scholastic.sbam.server.database.codegen.CancelReason;
import com.scholastic.sbam.server.database.objects.DbCancelReason;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.validation.AppCancelReasonValidator;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.CancelReasonInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateCancelReasonServiceImpl extends AuthenticatedServiceServlet implements UpdateCancelReasonService {

	@Override
	public UpdateResponse<CancelReasonInstance> updateCancelReason(CancelReasonInstance instance) throws IllegalArgumentException {
		
		boolean newCreated				= false;
		
		String	messages				= null;
		
		CancelReason dbInstance = null;
		
		@SuppressWarnings("unused")
		Authentication auth = authenticate("update cancel reasons", SecurityManager.ROLE_CONFIG);	// May later be used for logging activity
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Pre-edit/fix values
			validateInput(instance);
			
			//	Get existing, or create new
			if (instance.getCancelReasonCode() != null) {
				dbInstance = DbCancelReason.getByCode(instance.getCancelReasonCode());
			}

			//	If none found, create new
			if (dbInstance == null) {
				newCreated = true;
				dbInstance = new CancelReason();
				//	Set the create date/time
				dbInstance.setCreatedDatetime(new Date());
				dbInstance.setStatus('I');
			}

			//	Update values
			if (instance.getCancelReasonCode() != null)
				dbInstance.setCancelReasonCode(instance.getCancelReasonCode());
			if (instance.getDescription() != null)
				dbInstance.setDescription(instance.getDescription());
			if (instance.getStatus() != 0 && instance.getStatus() != dbInstance.getStatus())
				dbInstance.setStatus(instance.getStatus());
			if (instance.getChangeNotCancelChar() != dbInstance.getChangeNotCancel())
				dbInstance.setChangeNotCancel(instance.getChangeNotCancelChar());
//			if (instance.isChangeNotCancel() && dbInstance.getChangeNotCancel() != 'y')
//				dbInstance.setChangeNotCancel('y');
//			else
//				dbInstance.setChangeNotCancel('n');
			
			//	Persist in database
			DbCancelReason.persist(dbInstance);
			
			//	Refresh when new row is created, to get assigend ID
			if (newCreated) {
				DbCancelReason.refresh(dbInstance);	// This may not be necessary, but just in case
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
		
		return new UpdateResponse<CancelReasonInstance>(instance, messages);
	}
	
	private void validateInput(CancelReasonInstance instance) throws IllegalArgumentException {
//		testMessage(new AppUserNameValidator().validate(instance.getUserName()));
//	//	testMessage(new AppPasswordValidator().validate(instance.getPassword()));
//		testMessage(new EmailValidator().validate(instance.getEmail()));
//		testMessage(new AppRoleGroupValidator().validate(instance.getRoleGroupTitle()));
		AppCancelReasonValidator validator = new AppCancelReasonValidator();
		validator.setOriginal(instance);	//	This isn't really the original, but it's good enough, because it has the original ID
		testMessages(validator.validateCancelReason(instance));
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
