package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.UpdateAgreementSiteNoteService;
import com.scholastic.sbam.server.database.codegen.AgreementSite;
import com.scholastic.sbam.server.database.objects.DbAgreementSite;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.AgreementSiteInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateAgreementSiteNoteServiceImpl extends AuthenticatedServiceServlet implements UpdateAgreementSiteNoteService {

	@Override
	public UpdateResponse<AgreementSiteInstance> updateAgreementSiteNote(AgreementSiteInstance instance) throws IllegalArgumentException {
		
		if (instance.isNewRecord())
			throw new IllegalArgumentException("Notes can only be updated for an existing agreement site.");
		
		AgreementSite dbInstance = null;
		
		authenticate("update agreement site note", SecurityManager.ROLE_MAINT);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Get existing, or create new
			dbInstance = DbAgreementSite.getById(instance.getAgreementId(), instance.getSiteUcn(), instance.getSiteUcnSuffix(), instance.getSiteLocCode());
			if (dbInstance == null)
				throw new IllegalArgumentException("Agreement Site " + instance + " not found.");
				

			//	Update values
			
			if (instance.getNote() != null) {
				if (instance.getNote().equalsIgnoreCase("<br>"))
					instance.setNote("");
				dbInstance.setNote(instance.getNote());
			}
			
			//	Persist in database
			DbAgreementSite.persist(dbInstance);
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The agreement site note update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return new UpdateResponse<AgreementSiteInstance>(instance);
	}
	
	private void silentRollback() {
		try {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.getSession().getTransaction().rollback();	
		} catch (Exception exc) { }
	}
}
