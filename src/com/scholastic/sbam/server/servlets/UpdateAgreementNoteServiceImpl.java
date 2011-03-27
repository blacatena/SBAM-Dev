package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.UpdateAgreementNoteService;
import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.objects.DbAgreement;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateAgreementNoteServiceImpl extends AuthenticatedServiceServlet implements UpdateAgreementNoteService {

	@Override
	public UpdateResponse<AgreementInstance> updateAgreementNote(AgreementInstance instance) throws IllegalArgumentException {
		
		if (instance.isNewRecord() || instance.getId() <= 0)
			throw new IllegalArgumentException("Notes can only be updated for an existing agreement.");
		
		Agreement dbInstance = null;
		
		authenticate("update agreement note", SecurityManager.ROLE_MAINT);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Get existing, or create new
			dbInstance = DbAgreement.getById(instance.getId());
			if (dbInstance == null)
				throw new IllegalArgumentException("Agreement " + instance.getId() + " not found.");
				

			//	Update values
			
			if (instance.getNote() != null)
				dbInstance.setNote(instance.getNote());
			
			//	Persist in database
			DbAgreement.persist(dbInstance);
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The agreement note update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return new UpdateResponse<AgreementInstance>(instance);
	}
	
	private void silentRollback() {
		try {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.getSession().getTransaction().rollback();	
		} catch (Exception exc) { }
	}
}
