package com.scholastic.sbam.server.servlets;

import java.util.Date;
import java.util.List;

import com.scholastic.sbam.client.services.UpdateAgreementLinkService;
import com.scholastic.sbam.server.database.codegen.AgreementLink;
import com.scholastic.sbam.server.database.objects.DbAgreementLink;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.validation.AppAgreementLinkValidator;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.AgreementLinkInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateAgreementLinkServiceImpl extends AuthenticatedServiceServlet implements UpdateAgreementLinkService {

	@Override
	public UpdateResponse<AgreementLinkInstance> updateAgreementLink(AgreementLinkInstance instance) throws IllegalArgumentException {
		
		boolean newCreated				= false;
		
		String	messages				= null;
		
		AgreementLink dbInstance = null;
		
		authenticate("update proxy", SecurityManager.ROLE_MAINT);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Pre-edit/fix values
			validateInput(instance);
			
			//	Get existing, or create new
			if (instance.getLinkId() > 0 && !instance.isNewRecord()) {
				dbInstance = DbAgreementLink.getById(instance.getLinkId());
			}

			//	If none found, create new
			if (dbInstance == null) {
				newCreated = true;
				dbInstance = new AgreementLink();
				//	Set the create date/time and status
				dbInstance.setCreatedDatetime(new Date());
			}

			//	Update values

			if (instance.getUcn() > 0)
				dbInstance.setUcn(instance.getUcn());
			if (instance.getLinkTypeCode() != null)
				dbInstance.setLinkTypeCode(instance.getLinkTypeCode());
			if (instance.getStatus() != 0)
				dbInstance.setStatus(instance.getStatus());
			if (instance.getNote() != null) {
				if("<br>".equals(instance.getNote()))
					instance.setNote("");
				dbInstance.setNote(instance.getNote());
			}
				
			//	Fix any nulls
			if (instance.getLinkTypeCode() == null)
				dbInstance.setLinkTypeCode("");
			if (dbInstance.getStatus() == AppConstants.STATUS_ANY_NONE)
				dbInstance.setStatus(AppConstants.STATUS_ACTIVE);
			if (dbInstance.getNote() == null)
				dbInstance.setNote("");
			
			//	Persist in database
			DbAgreementLink.persist(dbInstance);
			
			//	Refresh when new row is created, to get assigned ID
			if (newCreated) {
			//	DbAgreementLink.refresh(dbInstance);	// This may not be necessary, but just in case
				instance.setLinkId(dbInstance.getLinkId());
				instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
				
				//	Need to set the check digit version, and update
				dbInstance.setLinkIdCheckDigit(AppConstants.appendCheckDigit(instance.getLinkId()));
				DbAgreementLink.persist(dbInstance);
			}
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The proxy update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return new UpdateResponse<AgreementLinkInstance>(instance, messages);
	}
	
	private void validateInput(AgreementLinkInstance instance) throws IllegalArgumentException {
		AppAgreementLinkValidator validator = new AppAgreementLinkValidator();
		validator.setOriginal(instance);	//	This isn't really the original, but it's good enough, because it has the original ID
		testMessages(validator.validateAgreementLink(instance));
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
