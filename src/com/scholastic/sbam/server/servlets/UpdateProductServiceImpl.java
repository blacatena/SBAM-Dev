package com.scholastic.sbam.server.servlets;

import java.util.Date;
import java.util.List;

import com.scholastic.sbam.client.services.UpdateProductService;
import com.scholastic.sbam.server.database.codegen.Product;
import com.scholastic.sbam.server.database.objects.DbProduct;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.validation.AppProductValidator;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.ProductInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateProductServiceImpl extends AuthenticatedServiceServlet implements UpdateProductService {

	@Override
	public UpdateResponse<ProductInstance> updateProduct(ProductInstance instance) throws IllegalArgumentException {
		
		boolean newCreated				= false;
		
		String	messages				= null;
		
		Product dbInstance = null;
		
		@SuppressWarnings("unused")
		Authentication auth = authenticate("update delete reasons", SecurityManager.ROLE_CONFIG);	// May later be used for logging activity
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Pre-edit/fix values
			validateInput(instance);
			
			//	Get existing, or create new
			if (instance.getProductCode() != null) {
				dbInstance = DbProduct.getByCode(instance.getProductCode());
			}

			//	If none found, create new
			if (dbInstance == null) {
				newCreated = true;
				dbInstance = new Product();
				//	Set the create date/time
				dbInstance.setCreatedDatetime(new Date());
				dbInstance.setStatus('I');
			}

			//	Update values
			if (instance.getProductCode() != null)
				dbInstance.setProductCode(instance.getProductCode());
			if (instance.getDescription() != null)
				dbInstance.setDescription(instance.getDescription());
			if (instance.getShortName() != null)
				dbInstance.setShortName(instance.getShortName());
			if (instance.getDefaultTermType() != (char) 0)
				dbInstance.setDefaultTermType(instance.getDefaultTermType());
			if (instance.getStatus() != 0 && instance.getStatus() != dbInstance.getStatus())
				dbInstance.setStatus(instance.getStatus());
			
			//	Persist in database
			DbProduct.persist(dbInstance);
			
			//	Refresh when new row is created, to get assigend ID
			if (newCreated) {
				DbProduct.refresh(dbInstance);	// This may not be necessary, but just in case
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
		
		return new UpdateResponse<ProductInstance>(instance, messages);
	}
	
	private void validateInput(ProductInstance instance) throws IllegalArgumentException {
		AppProductValidator validator = new AppProductValidator();
		validator.setOriginal(instance);	//	This isn't really the original, but it's good enough, because it has the original ID
		testMessages(validator.validateProduct(instance));
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
