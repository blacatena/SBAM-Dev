package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.UpdateProxyNoteService;
import com.scholastic.sbam.server.database.codegen.Proxy;
import com.scholastic.sbam.server.database.objects.DbProxy;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.ProxyInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateProxyNoteServiceImpl extends AuthenticatedServiceServlet implements UpdateProxyNoteService {

	@Override
	public UpdateResponse<ProxyInstance> updateProxyNote(ProxyInstance instance) throws IllegalArgumentException {
		
		if (instance.isNewRecord() || instance.getProxyId() <= 0)
			throw new IllegalArgumentException("Notes can only be updated for an existing proxy.");
		
		Proxy dbInstance = null;
		
		authenticate("update proxy note", SecurityManager.ROLE_MAINT);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Get existing, or create new
			dbInstance = DbProxy.getById(instance.getProxyId());
			if (dbInstance == null)
				throw new IllegalArgumentException("Proxy " + instance.getProxyId() + " not found.");
				

			//	Update values
			
			if (instance.getNote() != null) {
				if (instance.getNote().equalsIgnoreCase("<br>"))
					instance.setNote("");
				dbInstance.setNote(instance.getNote());
			}
			
			//	Persist in database
			DbProxy.persist(dbInstance);
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The proxy note update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return new UpdateResponse<ProxyInstance>(instance);
	}
	
	private void silentRollback() {
		try {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.getSession().getTransaction().rollback();	
		} catch (Exception exc) { }
	}
}
