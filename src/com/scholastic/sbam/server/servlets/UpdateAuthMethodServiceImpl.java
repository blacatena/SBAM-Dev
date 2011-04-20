package com.scholastic.sbam.server.servlets;

import java.util.Date;
import java.util.List;

import com.scholastic.sbam.client.services.UpdateAuthMethodService;
import com.scholastic.sbam.server.database.codegen.AuthMethod;
import com.scholastic.sbam.server.database.codegen.AuthMethodId;
import com.scholastic.sbam.server.database.objects.DbAuthMethod;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.validation.AppAuthMethodValidator;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateAuthMethodServiceImpl extends AuthenticatedServiceServlet implements UpdateAuthMethodService {

	@Override
	public UpdateResponse<AuthMethodInstance> updateAuthMethod(AuthMethodInstance instance) throws IllegalArgumentException {
		
//		boolean newCreated				= false;
		
		String	messages				= null;
		
		AuthMethod dbInstance = null;
		
		authenticate("update authentication method", SecurityManager.ROLE_MAINT);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Pre-edit/fix values
			validateInput(instance);
			
			//	Get existing, or create new
			if (instance.getMethodKey() > 0 && !instance.isNewRecord())
				dbInstance = DbAuthMethod.getById(instance.getAgreementId(), instance.getUcn(), instance.getUcnSuffix(), instance.getSiteLocCode(), instance.getMethodType(), instance.getMethodKey());
			
			//	If none found, create new
			if (dbInstance == null) {
//				newCreated = true;
				//	For a new entry, set the method type
				instance.syncMethodType();
				//	Create the new db instance with key
				dbInstance = new AuthMethod();
				AuthMethodId id = new AuthMethodId();
				id.setAgreementId(instance.getAgreementId());
				id.setUcn(instance.getUcn());
				id.setUcnSuffix(instance.getUcnSuffix());
				id.setSiteLocCode(instance.getSiteLocCode());
				id.setMethodType(instance.getMethodType());
				id.setMethodKey(DbAuthMethod.getNextAuthMethodKey(instance.getAgreementId(), instance.getUcn(), instance.getUcnSuffix(), instance.getSiteLocCode(), instance.getMethodType()));
				instance.setMethodKey(id.getMethodKey());
				dbInstance.setId(id);
				//	Set the create date/time and status
				dbInstance.setCreatedDatetime(new Date());
			}

			//	Update values
			
			if (instance.getStatus() != 0)
				dbInstance.setStatus(instance.getStatus());
			if (instance.getOrgPath() != null)
				dbInstance.setOrgPath(instance.getOrgPath());
			if (instance.getNote() != null)
				dbInstance.setNote(instance.getNote());
			
			if (instance.getForUcn() >= 0) {
				dbInstance.setForUcn(instance.getForUcn());
				if (instance.getForUcn() > 0) {
					dbInstance.setForUcnSuffix(instance.getForUcnSuffix());
					if (instance.getForSiteLocCode() != null)
						dbInstance.setForSiteLocCode(instance.getForSiteLocCode());
				} else {
					dbInstance.setForUcnSuffix(0);
					dbInstance.setForSiteLocCode("");
				}
			}
			
			if (instance.getIpLo() > 0)
				dbInstance.setIpLo(instance.getIpLo());
			if (instance.getIpHi() > 0)
				dbInstance.setIpHi(instance.getIpHi());
			if (instance.getUrl() != null)
				dbInstance.setUrl(instance.getUrl());
			if (instance.getUserId() != null)
				dbInstance.setUserId(instance.getUserId());
			if (instance.getPassword() != null)
				dbInstance.setPassword(instance.getPassword());
			if (instance.getUserType() != (char) 0)
				dbInstance.setUserType(instance.getUserType());
			if (instance.getProxyId() >= 0)
				dbInstance.setProxyId(instance.getProxyId());
				
			if (instance.getRemote() != (char) 0)
				dbInstance.setRemote(instance.getRemote());
			if (instance.getApproved() != (char) 0)
				dbInstance.setApproved(instance.getApproved());
			if (instance.getValidated() != (char) 0)
				dbInstance.setValidated(instance.getValidated());
			

			if (instance.getNote() != null) {
				if (instance.getNote().equalsIgnoreCase("<br>"))
					instance.setNote("");
				dbInstance.setNote(instance.getNote());
			}
			
			//	Fix any nulls
			if (dbInstance.getOrgPath() == null)
				dbInstance.setOrgPath("");
			if (dbInstance.getNote() == null)
				dbInstance.setNote("");
			if (instance.getUrl() == null)
				dbInstance.setUrl("");
			if (instance.getUserId() == null)
				dbInstance.setUserId("");
			if (instance.getPassword() == null)
				dbInstance.setPassword("");
			if (instance.getUserType() == (char) 0)
				dbInstance.setUserType(AuthMethodInstance.UserTypes.COOKIE.getCode().charAt(0));
				
			if (instance.getRemote() == (char) 0)
				dbInstance.setRemote('n');
			if (instance.getApproved() == (char) 0)
				dbInstance.setApproved('n');
			if (instance.getValidated() == (char) 0)
				dbInstance.setValidated('n');
			
			dbInstance.setUpdatedDatetime(new Date());
			
			//	Persist in database
			DbAuthMethod.persist(dbInstance);
			
			DbAuthMethod.setDescriptions(instance);
			
			//	Refresh when new row is created, to get assigned ID
//			if (newCreated) {
//			//	DbAuthMethod.refresh(dbInstance);	// This may not be necessary, but just in case
//			//	instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
//			}
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The authentication method update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return new UpdateResponse<AuthMethodInstance>(instance, messages);
	}
	
	private void validateInput(AuthMethodInstance instance) throws IllegalArgumentException {
		AppAuthMethodValidator validator = new AppAuthMethodValidator();
		validator.setOriginal(instance);	//	This isn't really the original, but it's good enough, because it has the original ID
		testMessages(validator.validateAuthMethod(instance));
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
