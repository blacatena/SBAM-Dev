package com.scholastic.sbam.server.servlets;

import java.util.Date;
import java.util.List;

import com.scholastic.sbam.client.services.UpdatePreferenceCategoryService;
import com.scholastic.sbam.server.database.codegen.PreferenceCategory;
import com.scholastic.sbam.server.database.objects.DbPreferenceCategory;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.validation.AppPreferenceCategoryValidator;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.PreferenceCategoryInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdatePreferenceCategoryServiceImpl extends AuthenticatedServiceServlet implements UpdatePreferenceCategoryService {

	@Override
	public UpdateResponse<PreferenceCategoryInstance> updatePreferenceCategory(PreferenceCategoryInstance instance) throws IllegalArgumentException {
		
		boolean newCreated				= false;
		
		String	messages				= null;
		
		PreferenceCategory dbInstance = null;
		
		@SuppressWarnings("unused")
		Authentication auth = authenticate("update preference categories", SecurityManager.ROLE_CONFIG);	// May later be used for logging activity
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Pre-edit/fix values
			validateInput(instance);
			
			//	Get existing, or create new
			if (instance.getPrefCatCode() != null) {
				dbInstance = DbPreferenceCategory.getByCode(instance.getPrefCatCode());
			}

			//	If none found, create new
			if (dbInstance == null) {
				newCreated = true;
				dbInstance = new PreferenceCategory();
				//	Set the create date/time
				dbInstance.setCreatedDatetime(new Date());
				dbInstance.setStatus('I');
			}

			//	Update values
			if (instance.getPrefCatCode() != null)
				dbInstance.setPrefCatCode(instance.getPrefCatCode());
			if (instance.getDescription() != null)
				dbInstance.setDescription(instance.getDescription());
			if (instance.getStatus() != 0 && instance.getStatus() != dbInstance.getStatus())
				dbInstance.setStatus(instance.getStatus());
			if (instance.getSeq() != dbInstance.getSeq())
				dbInstance.setSeq(instance.getSeq());
			
			//	Persist in database
			DbPreferenceCategory.persist(dbInstance);
			
			//	Refresh when new row is created, to get assigned ID or created date/time
			if (newCreated) {
			//	DbPreferenceCategory.refresh(dbInstance);	// This may not be necessary, but just in case
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
		
		return new UpdateResponse<PreferenceCategoryInstance>(instance, messages);
	}
	
	private void validateInput(PreferenceCategoryInstance instance) throws IllegalArgumentException {
		AppPreferenceCategoryValidator validator = new AppPreferenceCategoryValidator();
		validator.setOriginal(instance);	//	This isn't really the original, but it's good enough, because it has the original ID
		testMessages(validator.validatePreferenceCategory(instance));
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
