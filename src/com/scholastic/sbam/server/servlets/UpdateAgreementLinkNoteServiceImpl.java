package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.UpdateAgreementLinkNoteService;
import com.scholastic.sbam.server.database.codegen.AgreementLink;
import com.scholastic.sbam.server.database.objects.DbAgreementLink;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.AgreementLinkInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateAgreementLinkNoteServiceImpl extends AuthenticatedServiceServlet implements UpdateAgreementLinkNoteService {

	@Override
	public UpdateResponse<AgreementLinkInstance> updateAgreementLinkNote(AgreementLinkInstance instance) throws IllegalArgumentException {
		
		if (instance.isNewRecord() || instance.getLinkId() <= 0)
			throw new IllegalArgumentException("Notes can only be updated for an existing agreement link.");
		
		AgreementLink dbInstance = null;
		
		authenticate("update agreement link note", SecurityManager.ROLE_MAINT);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Get existing, or create new
			dbInstance = DbAgreementLink.getById(instance.getLinkId());
			if (dbInstance == null)
				throw new IllegalArgumentException("Agreement Link " + instance.getLinkId() + " not found.");
				

			//	Update values
			
			if (instance.getNote() != null) {
				if (instance.getNote().equalsIgnoreCase("<br>"))
					instance.setNote("");
				dbInstance.setNote(instance.getNote());
			}
			
			//	Persist in database
			DbAgreementLink.persist(dbInstance);
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The agreement link note update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return new UpdateResponse<AgreementLinkInstance>(instance);
	}
	
	private void silentRollback() {
		try {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.getSession().getTransaction().rollback();	
		} catch (Exception exc) { }
	}
}
