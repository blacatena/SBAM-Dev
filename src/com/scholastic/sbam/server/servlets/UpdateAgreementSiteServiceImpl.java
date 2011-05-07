package com.scholastic.sbam.server.servlets;

import java.util.Date;
import java.util.List;

import com.scholastic.sbam.client.services.UpdateAgreementSiteService;
import com.scholastic.sbam.server.database.codegen.AgreementSite;
import com.scholastic.sbam.server.database.codegen.AgreementSiteId;
import com.scholastic.sbam.server.database.objects.DbAgreementSite;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.SiteInstitutionCache;
import com.scholastic.sbam.server.validation.AppAgreementSiteValidator;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.AgreementSiteInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateAgreementSiteServiceImpl extends AuthenticatedServiceServlet implements UpdateAgreementSiteService {

	@Override
	public UpdateResponse<AgreementSiteInstance> updateAgreementSite(AgreementSiteInstance instance) throws IllegalArgumentException {
		
		boolean newCreated				= false;
		
		String	messages				= null;
		
		AgreementSite dbInstance = null;
		
		authenticate("update agreement site", SecurityManager.ROLE_MAINT);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Pre-edit/fix values
			validateInput(instance);
			
			//	Get existing, or create new
			if (instance.getAgreementId() > 0 && instance.getSiteUcn() > 0) {
				dbInstance = DbAgreementSite.getById(instance.getAgreementId(), instance.getSiteUcn(), instance.getSiteUcnSuffix(), instance.getSiteLocCode());
			}

			//	If none found, create new
			if (dbInstance == null) {
				newCreated = true;
				dbInstance = new AgreementSite();
				AgreementSiteId id = new AgreementSiteId();
				id.setAgreementId(instance.getAgreementId());
				id.setSiteUcn(instance.getSiteUcn());
				id.setSiteUcnSuffix(instance.getSiteUcnSuffix());
				id.setSiteLocCode(instance.getSiteLocCode());
				dbInstance.setId(id);
				//	Set the create date/time and status
				dbInstance.setCreatedDatetime(new Date());
			}

			//	Update values
			
			if (instance.getStatus() != 0)
				dbInstance.setStatus(instance.getStatus());
			if (instance.getCommissionCode() != null)
				dbInstance.setCommissionCode(instance.getCommissionCode());
			if (instance.getCancelReasonCode() != null)
				dbInstance.setCancelReasonCode(instance.getCancelReasonCode());
			if (instance.getOrgPath() != null)
				dbInstance.setOrgPath(instance.getOrgPath());
			if (instance.getNote() != null)
				dbInstance.setNote(instance.getNote());

			if (instance.getActiveDate() != null)
				dbInstance.setActiveDate(instance.getActiveDate());
			if (instance.getInactiveDate() != null)
				dbInstance.setInactiveDate(instance.getInactiveDate());
				
			//	Fix any nulls
			if (dbInstance.getCancelReasonCode() == null)
				dbInstance.setCancelReasonCode("");
			if (dbInstance.getOrgPath() == null)
				dbInstance.setOrgPath("");
			if (dbInstance.getNote() == null)
				dbInstance.setNote("");
			
			//	Fix cancel values
			if (dbInstance.getCancelReasonCode() != null && dbInstance.getCancelReasonCode().length() > 0) {
				if (dbInstance.getInactiveDate() == null)
					dbInstance.setInactiveDate(new Date());
				if (dbInstance.getStatus() != AppConstants.STATUS_DELETED)	// If it's already marked DELETED, then that's better than cancelled, so leave it
					dbInstance.setStatus(AppConstants.STATUS_INACTIVE);
			} else {
				if (dbInstance.getInactiveDate() != null)
					dbInstance.setInactiveDate(null);
				if (dbInstance.getStatus() == AppConstants.STATUS_INACTIVE)
					dbInstance.setStatus(AppConstants.STATUS_ACTIVE);
			}
			
			//	Persist in database
			DbAgreementSite.persist(dbInstance);
			
			//	Refresh when new row is created, to get assigned ID
			if (newCreated) {
			//	DbAgreementSite.refresh(dbInstance);	// This may not be necessary, but just in case
				instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
				DbAgreementSite.setDescriptions(instance);
			}
			
			//	Finally, record the customer in the customer cache
			if (SiteInstitutionCache.getSingleton() != null) {
				InstitutionInstance institution = instance.getSite().getInstitution();
				if (institution == null && instance.getSiteUcn() > 0) {
					institution = DbInstitution.getInstance(DbInstitution.getByCode(instance.getSiteUcn()));
				}
				if (institution != null)
					SiteInstitutionCache.getSingleton().addInstitution(institution);
			}
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The agreement site update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return new UpdateResponse<AgreementSiteInstance>(instance, messages);
	}
	
	private void validateInput(AgreementSiteInstance instance) throws IllegalArgumentException {
		AppAgreementSiteValidator validator = new AppAgreementSiteValidator();
		validator.setOriginal(instance);	//	This isn't really the original, but it's good enough, because it has the original ID
		testMessages(validator.validateAgreementSite(instance));
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
