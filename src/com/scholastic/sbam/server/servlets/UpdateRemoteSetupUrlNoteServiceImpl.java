package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.UpdateRemoteSetupUrlNoteService;
import com.scholastic.sbam.server.database.codegen.RemoteSetupUrl;
import com.scholastic.sbam.server.database.objects.DbRemoteSetupUrl;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.RemoteSetupUrlInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateRemoteSetupUrlNoteServiceImpl extends AuthenticatedServiceServlet implements UpdateRemoteSetupUrlNoteService {

	@Override
	public UpdateResponse<RemoteSetupUrlInstance> updateRemoteSetupUrlNote(RemoteSetupUrlInstance instance) throws IllegalArgumentException {
		
		if (instance.isNewRecord())
			throw new IllegalArgumentException("Notes can only be updated for an existing remote setup url.");
		
		RemoteSetupUrl dbInstance = null;
		
		authenticate("update remote setup url note", SecurityManager.ROLE_MAINT);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Get existing
			dbInstance = DbRemoteSetupUrl.getById(instance.getAgreementId(), instance.getUcn(), instance.getUcnSuffix(), instance.getSiteLocCode(), instance.getUrlId());
			if (dbInstance == null)
				throw new IllegalArgumentException("Remote setup URL " + instance + " not found.");
				

			//	Update values
			
			if (instance.getNote() != null) {
				if (instance.getNote().equalsIgnoreCase("<br>"))
					instance.setNote("");
				dbInstance.setNote(instance.getNote());
			}
			
			//	Persist in database
			DbRemoteSetupUrl.persist(dbInstance);
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The remote setup URL note update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return new UpdateResponse<RemoteSetupUrlInstance>(instance);
	}
	
	private void silentRollback() {
		try {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.getSession().getTransaction().rollback();	
		} catch (Exception exc) { }
	}
}
