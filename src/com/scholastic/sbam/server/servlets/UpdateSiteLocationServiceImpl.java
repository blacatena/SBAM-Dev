package com.scholastic.sbam.server.servlets;

import java.util.Date;
import java.util.List;

import com.scholastic.sbam.client.services.UpdateSiteLocationService;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.codegen.SiteId;
import com.scholastic.sbam.server.database.objects.DbSite;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.validation.AppSiteValidator;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateSiteLocationServiceImpl extends AuthenticatedServiceServlet implements UpdateSiteLocationService {

	@Override
	public UpdateResponse<SiteInstance> updateSiteLocation(SiteInstance instance) throws IllegalArgumentException {
		
		boolean newCreated				= false;
		
		String	messages				= null;
		
		Site dbInstance = null;
		
		authenticate("update site", SecurityManager.ROLE_MAINT);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Pre-edit/fix values
			validateInput(instance);
			
			//	Get existing, or create new
			if (instance.getUcn() > 0) {
				dbInstance = DbSite.getById(instance.getUcn(), instance.getUcnSuffix(), instance.getSiteLocCode());
			}

			//	If none found, create new
			if (dbInstance == null) {
				newCreated = true;
				dbInstance = new Site();
				SiteId id = new SiteId();
				id.setUcn(instance.getUcn());
				id.setUcnSuffix(instance.getUcnSuffix());
				id.setSiteLocCode(instance.getSiteLocCode());
				dbInstance.setId(id);
				//	Set the create date/time and status
				dbInstance.setCreatedDatetime(new Date());
			}

			//	Update values

			if (instance.getDescription() != null)
				dbInstance.setDescription(instance.getDescription());
			if (instance.getStatus() != 0)
				dbInstance.setStatus(instance.getStatus());
			if (instance.getCommissionCode() != null)
				dbInstance.setCommissionCode(instance.getCommissionCode());
			if (instance.getPseudoSite() != 0)
				dbInstance.setPseudoSite(instance.getPseudoSite());
			if (instance.getNote() != null)
				dbInstance.setNote(instance.getNote());
				
			//	Fix any nulls
			if (instance.getDescription() == null)
				dbInstance.setDescription("Missing Description");
			if (dbInstance.getStatus() == AppConstants.STATUS_ANY_NONE)
				dbInstance.setStatus(AppConstants.STATUS_ACTIVE);
			if (dbInstance.getCommissionCode() == null)
				dbInstance.setCommissionCode("");
			if (dbInstance.getPseudoSite() == 0)
				dbInstance.setPseudoSite('n');
			if (dbInstance.getNote() == null)
				dbInstance.setNote("");
			
			//	Persist in database
			DbSite.persist(dbInstance);
			
			//	Refresh when new row is created, to get assigned ID
			if (newCreated) {
			//	DbSite.refresh(dbInstance);	// This may not be necessary, but just in case
				instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
				DbSite.setDescriptions(instance);
			}
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The site update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return new UpdateResponse<SiteInstance>(instance, messages);
	}
	
	private void validateInput(SiteInstance instance) throws IllegalArgumentException {
		AppSiteValidator validator = new AppSiteValidator();
		validator.setOriginal(instance);	//	This isn't really the original, but it's good enough, because it has the original ID
		testMessages(validator.validateSite(instance));
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
