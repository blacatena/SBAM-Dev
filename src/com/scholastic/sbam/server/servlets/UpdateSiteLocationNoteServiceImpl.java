package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.UpdateSiteLocationNoteService;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.objects.DbSite;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateSiteLocationNoteServiceImpl extends AuthenticatedServiceServlet implements UpdateSiteLocationNoteService {

	@Override
	public UpdateResponse<SiteInstance> updateSiteLocationNote(SiteInstance instance) throws IllegalArgumentException {
		
		if (instance.isNewRecord())
			throw new IllegalArgumentException("Notes can only be updated for an existing agreement site.");
		
		Site dbInstance = null;
		
		authenticate("update agreement site note", SecurityManager.ROLE_MAINT);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Get existing, or create new
			dbInstance = DbSite.getById(instance.getUcn(), instance.getUcnSuffix(), instance.getSiteLocCode());
			if (dbInstance == null)
				throw new IllegalArgumentException("Site Location " + instance + " not found.");
				

			//	Update values
			
			if (instance.getNote() != null) {
				if (instance.getNote().equalsIgnoreCase("<br>"))
					instance.setNote("");
				dbInstance.setNote(instance.getNote());
			}
			
			//	Persist in database
			DbSite.persist(dbInstance);
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The site location note update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return new UpdateResponse<SiteInstance>(instance);
	}
	
	private void silentRollback() {
		try {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.getSession().getTransaction().rollback();	
		} catch (Exception exc) { }
	}
}
