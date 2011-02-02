package com.scholastic.sbam.server.servlets;

import java.util.Date;
import java.util.List;

import com.scholastic.sbam.client.services.UpdateCommissionTypeService;
import com.scholastic.sbam.server.database.codegen.CommissionType;
import com.scholastic.sbam.server.database.objects.DbCommissionType;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.validation.AppCommissionTypeValidator;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateCommissionTypeServiceImpl extends AuthenticatedServiceServlet implements UpdateCommissionTypeService {

	@Override
	public UpdateResponse<CommissionTypeInstance> updateCommissionType(CommissionTypeInstance instance) throws IllegalArgumentException {
		
		boolean newCreated				= false;
		
		String	messages				= null;
		
		CommissionType dbInstance = null;
		
		@SuppressWarnings("unused")
		Authentication auth = authenticate("update commission types", SecurityManager.ROLE_CONFIG);	// May later be used for logging activity
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Pre-edit/fix values
			validateInput(instance);
			
			//	Get existing, or create new
			if (instance.getCommissionCode() != null) {
				dbInstance = DbCommissionType.getByCode(instance.getCommissionCode());
			}

			//	If none found, create new
			if (dbInstance == null) {
				newCreated = true;
				dbInstance = new CommissionType();
				//	Set the create date/time
				dbInstance.setCreatedDatetime(new Date());
				dbInstance.setStatus('I');
			}

			//	Update values
			if (instance.getCommissionCode() != null)
				dbInstance.setCommissionCode(instance.getCommissionCode());
			if (instance.getDescription() != null)
				dbInstance.setDescription(instance.getDescription());
			if (instance.getShortName() != null)
				dbInstance.setShortName(instance.getShortName());
			if (instance.getStatus() != 0 && instance.getStatus() != dbInstance.getStatus())
				dbInstance.setStatus(instance.getStatus());
			if (instance.getProductsChar() != dbInstance.getProducts())
				dbInstance.setProducts(instance.getProductsChar());
			if (instance.getSitesChar() != dbInstance.getSites())
				dbInstance.setSites(instance.getSitesChar());
			if (instance.getAgreementsChar() != dbInstance.getAgreements())
				dbInstance.setAgreements(instance.getAgreementsChar());
			if (instance.getAgreementTermsChar() != dbInstance.getAgreementTerms())
				dbInstance.setAgreementTerms(instance.getAgreementTermsChar());
			
			//	Persist in database
			DbCommissionType.persist(dbInstance);
			DbCommissionType.commit();
			
			//	Refresh when new row is created, to get create date/time
			if (newCreated) {
				HibernateUtil.startTransaction();
				DbCommissionType.refresh(dbInstance);	// This may not be necessary, but just in case
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
		
		return new UpdateResponse<CommissionTypeInstance>(instance, messages);
	}
	
	private void validateInput(CommissionTypeInstance instance) throws IllegalArgumentException {
		AppCommissionTypeValidator validator = new AppCommissionTypeValidator();
		validator.setOriginal(instance);	//	This isn't really the original, but it's good enough, because it has the original ID
		testMessages(validator.validateCommissionType(instance));
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
