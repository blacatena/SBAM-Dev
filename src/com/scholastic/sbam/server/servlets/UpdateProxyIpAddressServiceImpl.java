package com.scholastic.sbam.server.servlets;

import java.util.Date;
import java.util.List;

import com.scholastic.sbam.client.services.UpdateProxyIpAddressService;
import com.scholastic.sbam.server.database.codegen.ProxyIp;
import com.scholastic.sbam.server.database.codegen.ProxyIpId;
import com.scholastic.sbam.server.database.objects.DbProxyIp;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.validation.AppProxyIpValidator;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.ProxyIpInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateProxyIpAddressServiceImpl extends AuthenticatedServiceServlet implements UpdateProxyIpAddressService {

	@Override
	public UpdateResponse<ProxyIpInstance> updateProxyIpAddress(ProxyIpInstance instance) throws IllegalArgumentException {
		
		boolean newCreated				= false;
		
		String	messages				= null;
		
		ProxyIp dbInstance = null;
		
		authenticate("update proxy IP", SecurityManager.ROLE_MAINT);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Pre-edit/fix values
			validateInput(instance);
			
			//	Get existing, or create new
			if (instance.getIpId() > 0 && !instance.isNewRecord())
				dbInstance = DbProxyIp.getById(instance.getProxyId(), instance.getIpId());
			
			//	If none found, create new
			if (dbInstance == null) {
				newCreated = true;
				//	Create the new db instance with key
				dbInstance = new ProxyIp();
				ProxyIpId id = new ProxyIpId();
				id.setProxyId(instance.getProxyId());
				id.setIpId(DbProxyIp.getNextIpId(instance.getProxyId()));
				dbInstance.setId(id);
				//	Set the create date/time and status
				dbInstance.setCreatedDatetime(new Date());
			}

			//	Update values
			
			if (instance.getStatus() != 0)
				dbInstance.setStatus(instance.getStatus());
			if (instance.getNote() != null)
				dbInstance.setNote(instance.getNote());
			
			if (instance.getIpLo() > 0)
				dbInstance.setIpLo(instance.getIpLo());
			if (instance.getIpHi() > 0)
				dbInstance.setIpHi(instance.getIpHi());
			if (instance.getIpRangeCode() != null)
				dbInstance.setIpRangeCode(instance.getIpRangeCode());
				
			if (instance.getApproved() != (char) 0)
				dbInstance.setApproved(instance.getApproved());
			

			if (instance.getNote() != null) {
				if (instance.getNote().equalsIgnoreCase("<br>"))
					instance.setNote("");
				dbInstance.setNote(instance.getNote());
			}
			
			//	Fix any nulls
			if (dbInstance.getIpRangeCode() == null)
				dbInstance.setIpRangeCode("");
			if (dbInstance.getNote() == null)
				dbInstance.setNote("");
				
			if (instance.getApproved() == (char) 0)
				dbInstance.setApproved('n');
			
			//	Persist in database
			DbProxyIp.persist(dbInstance);
			
			//	Refresh when new row is created, to get assigned ID
			if (newCreated) {				
//			//	DbProxyIp.refresh(dbInstance);	// This may not be necessary, but just in case
				instance.setIpId(dbInstance.getId().getIpId());
				instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
			}
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The proxy IP update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return new UpdateResponse<ProxyIpInstance>(instance, messages);
	}
	
	private void validateInput(ProxyIpInstance instance) throws IllegalArgumentException {
		AppProxyIpValidator validator = new AppProxyIpValidator();
		validator.setOriginal(instance);	//	This isn't really the original, but it's good enough, because it has the original ID
		testMessages(validator.validateProxyIp(instance));
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
