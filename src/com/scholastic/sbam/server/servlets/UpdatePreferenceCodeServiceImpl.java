package com.scholastic.sbam.server.servlets;

import java.util.Date;
import java.util.List;

import com.scholastic.sbam.client.services.UpdatePreferenceCodeService;
import com.scholastic.sbam.server.database.codegen.PreferenceCode;
import com.scholastic.sbam.server.database.codegen.PreferenceCodeId;
import com.scholastic.sbam.server.database.objects.DbPreferenceCode;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.validation.AppPreferenceCodeValidator;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.PreferenceCodeInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdatePreferenceCodeServiceImpl extends AuthenticatedServiceServlet implements UpdatePreferenceCodeService {

	@Override
	public UpdateResponse<PreferenceCodeInstance> updatePreferenceCode(PreferenceCodeInstance instance) throws IllegalArgumentException {
		
		boolean newCreated				= false;
		
		String	messages				= null;
		
		PreferenceCode dbInstance = null;
		
		@SuppressWarnings("unused")
		Authentication auth = authenticate("update preference categories", SecurityManager.ROLE_CONFIG);	// May later be used for logging activity
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Pre-edit/fix values
			validateInput(instance);
			
			//	Get existing, or create new
			if (instance.getPrefCatCode() != null && instance.getPrefSelCode() != null) {
				dbInstance = DbPreferenceCode.getByCode(instance.getPrefCatCode(), instance.getPrefSelCode());
			}

			//	If none found, create new
			if (dbInstance == null) {
				newCreated = true;
				dbInstance = new PreferenceCode();
				//	Set the create date/time
				dbInstance.setCreatedDatetime(new Date());
				dbInstance.setStatus('I');
				//	Create ID
				dbInstance.setId(new PreferenceCodeId());
			}

			//	Update values
			if (instance.getPrefCatCode() != null)
				dbInstance.getId().setPrefCatCode(instance.getPrefCatCode());
			if (instance.getPrefSelCode() != null)
				dbInstance.getId().setPrefSelCode(instance.getPrefSelCode());
			if (instance.getDescription() != null)
				dbInstance.setDescription(instance.getDescription());
			if (instance.getExportValue() != null)
				dbInstance.setExportValue(instance.getExportValue());
			if (instance.getStatus() != 0 && instance.getStatus() != dbInstance.getStatus())
				dbInstance.setStatus(instance.getStatus());
			if (instance.getSeq() != dbInstance.getSeq())
				dbInstance.setSeq(instance.getSeq());
			
			//	Persist in database
			DbPreferenceCode.persist(dbInstance);
			
			//	Refresh when new row is created, to get assigned ID or created date/time
			if (newCreated) {
			//	DbPreferenceCode.refresh(dbInstance);	// This may not be necessary, but just in case
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
		
		return new UpdateResponse<PreferenceCodeInstance>(instance, messages);
	}
	
	private void validateInput(PreferenceCodeInstance instance) throws IllegalArgumentException {
		AppPreferenceCodeValidator validator = new AppPreferenceCodeValidator();
		validator.setOriginal(instance);	//	This isn't really the original, but it's good enough, because it has the original ID
		testMessages(validator.validatePreferenceCode(instance));
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
