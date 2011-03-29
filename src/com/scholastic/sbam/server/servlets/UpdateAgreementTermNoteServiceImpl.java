package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.UpdateAgreementTermNoteService;
import com.scholastic.sbam.server.database.codegen.AgreementTerm;
import com.scholastic.sbam.server.database.objects.DbAgreementTerm;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateAgreementTermNoteServiceImpl extends AuthenticatedServiceServlet implements UpdateAgreementTermNoteService {

	@Override
	public UpdateResponse<AgreementTermInstance> updateAgreementTermNote(AgreementTermInstance instance) throws IllegalArgumentException {
		
		if (instance.isNewRecord() || instance.getId() <= 0)
			throw new IllegalArgumentException("Notes can only be updated for an existing agreement term.");
		
		AgreementTerm dbInstance = null;
		
		authenticate("update agreement term note", SecurityManager.ROLE_MAINT);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Get existing, or create new
			dbInstance = DbAgreementTerm.getById(instance.getAgreementId(), instance.getId());
			if (dbInstance == null)
				throw new IllegalArgumentException("Agreement Term " + instance.getId() + " not found.");
				

			//	Update values
			
			if (instance.getNote() != null) {
				if (instance.getNote().equalsIgnoreCase("<br>"))
					instance.setNote("");
				dbInstance.setNote(instance.getNote());
			}
			
			//	Persist in database
			DbAgreementTerm.persist(dbInstance);
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The agreement term note update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return new UpdateResponse<AgreementTermInstance>(instance);
	}
	
	private void silentRollback() {
		try {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.getSession().getTransaction().rollback();	
		} catch (Exception exc) { }
	}
}
