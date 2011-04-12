package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.UpdateAuthMethodNoteService;
import com.scholastic.sbam.server.database.codegen.AuthMethod;
import com.scholastic.sbam.server.database.objects.DbAuthMethod;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateAuthMethodNoteServiceImpl extends AuthenticatedServiceServlet implements UpdateAuthMethodNoteService {

	@Override
	public UpdateResponse<AuthMethodInstance> updateAuthMethodNote(AuthMethodInstance instance) throws IllegalArgumentException {
		
		if (instance.isNewRecord())
			throw new IllegalArgumentException("Notes can only be updated for an existing authentication method.");
		
		AuthMethod dbInstance = null;
		
		authenticate("update authentication method note", SecurityManager.ROLE_MAINT);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Get existing, or create new
			dbInstance = DbAuthMethod.getById(instance.getAgreementId(), instance.getUcn(), instance.getUcnSuffix(), instance.getSiteLocCode(), instance.getMethodType(), instance.getMethodKey());
			if (dbInstance == null)
				throw new IllegalArgumentException("Authentication Method " + instance + " not found.");
				

			//	Update values
			
			if (instance.getNote() != null) {
				if (instance.getNote().equalsIgnoreCase("<br>"))
					instance.setNote("");
				dbInstance.setNote(instance.getNote());
			}
			
			//	Persist in database
			DbAuthMethod.persist(dbInstance);
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The authentication method note update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return new UpdateResponse<AuthMethodInstance>(instance);
	}
	
	private void silentRollback() {
		try {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.getSession().getTransaction().rollback();	
		} catch (Exception exc) { }
	}
}
