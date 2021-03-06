package com.scholastic.sbam.server.servlets;

import java.util.Date;
import java.util.List;

import com.scholastic.sbam.client.services.UpdateRemoteSetupUrlService;
import com.scholastic.sbam.server.database.codegen.RemoteSetupUrl;
import com.scholastic.sbam.server.database.codegen.RemoteSetupUrlId;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.objects.DbRemoteSetupUrl;
import com.scholastic.sbam.server.database.objects.DbSite;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.validation.AppRemoteSetupUrlValidator;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.RemoteSetupUrlInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateRemoteSetupUrlServiceImpl extends AuthenticatedServiceServlet implements UpdateRemoteSetupUrlService {

	@Override
	public UpdateResponse<RemoteSetupUrlInstance> updateRemoteSetupUrl(RemoteSetupUrlInstance instance) throws IllegalArgumentException {
		
//		boolean newCreated				= false;
		
		String	messages				= null;
		
		RemoteSetupUrl dbInstance = null;
		
		authenticate("update remote setup url", SecurityManager.ROLE_MAINT);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Pre-edit/fix values
			validateInput(instance);
			
			//	Get existing, or create new
			if (instance.getUrlId() > 0 && !instance.isNewRecord())
				dbInstance = DbRemoteSetupUrl.getById(instance.getAgreementId(), instance.getUcn(), instance.getUcnSuffix(), instance.getSiteLocCode(), instance.getUrlId());
			else {
				//	Look for deleted
				dbInstance = DbRemoteSetupUrl.getByOwnerUrl(instance.getAgreementId(), instance.getUcn(), instance.getUcnSuffix(), instance.getSiteLocCode(), instance.getUrl());
			}
			
			//	If none found, create new
			if (dbInstance == null) {
//				newCreated = true;
				//	Create the new db instance with key
				dbInstance = new RemoteSetupUrl();
				RemoteSetupUrlId id = new RemoteSetupUrlId();
				id.setAgreementId(instance.getAgreementId());
				id.setUcn(instance.getUcn());
				id.setUcnSuffix(instance.getUcnSuffix());
				id.setSiteLocCode(instance.getSiteLocCode());
				id.setUrlId(DbRemoteSetupUrl.getNextRemoteSetupUrlId(instance.getAgreementId(), instance.getUcn(), instance.getUcnSuffix(), instance.getSiteLocCode()));
				instance.setUrlId(id.getUrlId());
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
			
			if (instance.getUrl() != null)
				dbInstance.setUrl(instance.getUrl());
				
			if (instance.getApproved() != (char) 0)
				dbInstance.setApproved(instance.getApproved());
			

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
				
			if (instance.getApproved() == (char) 0)
				dbInstance.setApproved('n');
			
			//	Persist in database
			DbRemoteSetupUrl.persist(dbInstance);
			
			//	Create the site if necessary
			if (instance.getForUcn() > 0 && instance.getForSiteLocCode() != null && instance.getForSiteLocCode().length() > 0) {
				Site site = DbSite.getById(instance.getForUcn(), instance.getForUcnSuffix(), instance.getForSiteLocCode());
				if (site == null) {
					UpdateSiteLocationServiceImpl.updateSite(SiteInstance.getDefaultNewInstance(instance.getForUcn(), instance.getForUcnSuffix(), instance.getForSiteLocCode()));
				} else if (site.getStatus() == AppConstants.STATUS_DELETED) {
					SiteInstance siteInstance = DbSite.getInstance(site);
					siteInstance.setStatus(AppConstants.STATUS_ACTIVE);
					UpdateSiteLocationServiceImpl.updateSite(siteInstance);
				}
			}
			
			//	Fill in the related instances
			DbRemoteSetupUrl.setDescriptions(instance);
			
			//	Refresh when new row is created, to get assigned ID
//			if (newCreated) {
//			//	DbRemoteSetupUrl.refresh(dbInstance);	// This may not be necessary, but just in case
//			//	instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
//			}
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The remote setup url update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return new UpdateResponse<RemoteSetupUrlInstance>(instance, messages);
	}
	
	private void validateInput(RemoteSetupUrlInstance instance) throws IllegalArgumentException {
		AppRemoteSetupUrlValidator validator = new AppRemoteSetupUrlValidator();
		validator.setOriginal(instance);	//	This isn't really the original, but it's good enough, because it has the original ID
		testMessages(validator.validateRemoteSetupUrl(instance));
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
