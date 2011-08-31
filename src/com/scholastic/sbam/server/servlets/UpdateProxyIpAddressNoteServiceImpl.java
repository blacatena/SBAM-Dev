package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.UpdateProxyIpAddressNoteService;
import com.scholastic.sbam.server.database.codegen.ProxyIp;
import com.scholastic.sbam.server.database.objects.DbProxyIp;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.ProxyIpInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateProxyIpAddressNoteServiceImpl extends AuthenticatedServiceServlet implements UpdateProxyIpAddressNoteService {

	@Override
	public UpdateResponse<ProxyIpInstance> updateProxyIpAddressNote(ProxyIpInstance instance) throws IllegalArgumentException {
		
		if (instance.isNewRecord() || instance.getProxyId() <= 0 || instance.getIpId() <= 0)
			throw new IllegalArgumentException("Notes can only be updated for an existing proxy IP.");
		
		ProxyIp dbInstance = null;
		
		authenticate("update proxy IP note", SecurityManager.ROLE_MAINT);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Get existing, or create new
			dbInstance = DbProxyIp.getById(instance.getProxyId(), instance.getIpId());
			if (dbInstance == null)
				throw new IllegalArgumentException("ProxyIp " + instance.getProxyId() + ":" + instance.getIpId() + " not found.");
				

			//	Update values
			
			if (instance.getNote() != null) {
				if (instance.getNote().equalsIgnoreCase("<br>"))
					instance.setNote("");
				dbInstance.setNote(instance.getNote());
			}
			
			//	Persist in database
			DbProxyIp.persist(dbInstance);
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The proxy IP note update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return new UpdateResponse<ProxyIpInstance>(instance);
	}
	
	private void silentRollback() {
		try {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.getSession().getTransaction().rollback();	
		} catch (Exception exc) { }
	}
}
