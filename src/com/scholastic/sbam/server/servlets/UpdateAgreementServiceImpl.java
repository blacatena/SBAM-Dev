package com.scholastic.sbam.server.servlets;

import java.util.Date;
import java.util.List;

import com.scholastic.sbam.client.services.UpdateAgreementService;
import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.AgreementSite;
import com.scholastic.sbam.server.database.codegen.AgreementSiteId;
import com.scholastic.sbam.server.database.objects.DbAgreement;
import com.scholastic.sbam.server.database.objects.DbAgreementSite;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.server.validation.AppAgreementValidator;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateAgreementServiceImpl extends AuthenticatedServiceServlet implements UpdateAgreementService {
	/**
	 * Set to TRUE to automatically add the bill to UCN as a site for a new agreement.
	 */
	public static final boolean		AUTO_ADD_BILL_UCN_AS_SITE = false;

	@Override
	public UpdateResponse<AgreementInstance> updateAgreement(AgreementInstance instance) throws IllegalArgumentException {
		
		boolean newCreated				= false;
		
		String	messages				= null;
		
		Agreement dbInstance = null;
		
		authenticate("update agreement", SecurityManager.ROLE_MAINT);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Pre-edit/fix values
			validateInput(instance);
			
			//	Get existing, or create new
			if (instance.getId() > 0) {
				dbInstance = DbAgreement.getById(instance.getId());
			}

			//	If none found, create new
			if (dbInstance == null) {
				newCreated = true;
				dbInstance = new Agreement();
				//	Set the create date/time and status
				dbInstance.setCreatedDatetime(new Date());
			}

			//	Update values
			
			if (instance.getStatus() != 0)
				dbInstance.setStatus(instance.getStatus());
			if (instance.getBillUcn() > 0)
				dbInstance.setBillUcn(instance.getBillUcn());
			if (instance.getBillUcnSuffix() > 0)
				dbInstance.setBillUcnSuffix(instance.getBillUcnSuffix());
			if (instance.getAgreementTypeCode() != null && instance.getAgreementTypeCode().length() > 0)
				dbInstance.setAgreementTypeCode(instance.getAgreementTypeCode());
			if (instance.getCommissionCode() != null && instance.getCommissionCode().length() > 0)
				dbInstance.setCommissionCode(instance.getCommissionCode());
			if (instance.getDeleteReasonCode() != null)
				dbInstance.setDeleteReasonCode(instance.getDeleteReasonCode());
			if (instance.getOrgPath() != null)
				dbInstance.setOrgPath(instance.getOrgPath());
			if (instance.getNote() != null)
				dbInstance.setNote(instance.getNote());
			
			if (instance.getAgreementLinkId() != 0)
				dbInstance.setAgreementLinkId(instance.getAgreementLinkId());	//	This could be negative, to save the old link ID without it having any effect
			
			if (instance.getBuildings() >= 0)
				dbInstance.setBuildings(instance.getBuildings());
			if (instance.getEnrollment() >= 0)
				dbInstance.setEnrollment(instance.getEnrollment());
			if (instance.getPopulation() >= 0)
				dbInstance.setPopulation(instance.getPopulation());
			if (instance.getWorkstations() >= 0)
				dbInstance.setWorkstations(instance.getWorkstations());
			
			
			//	Fix any nulls
			if (dbInstance.getDeleteReasonCode() == null)
				dbInstance.setDeleteReasonCode("");
			if (dbInstance.getOrgPath() == null)
				dbInstance.setOrgPath("");
			if (dbInstance.getNote() == null)
				dbInstance.setNote("");
			
			//	Persist in database
			DbAgreement.persist(dbInstance);
			
			//	Refresh when new row is created, to get assigned ID
			if (newCreated) {
			//	DbAgreement.refresh(dbInstance);	// This may not be necessary, but just in case
				instance.setId(dbInstance.getId());
				instance.setIdCheckDigit(AppConstants.appendCheckDigit(instance.getId()));
				instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
				DbAgreement.setDescriptions(instance);
				if (instance.getInstitution() != null) {
					InstitutionCache.getSingleton().setDescriptions( instance.getInstitution() );
				}
				
				//	Update the check digit in the database based on the now assigned agreement ID
				dbInstance.setIdCheckDigit(instance.getIdCheckDigit());
				DbAgreement.persist(dbInstance);
			}
			
			if (newCreated) {
				autoCreateSite(instance, AUTO_ADD_BILL_UCN_AS_SITE);
			}
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The agreement update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return new UpdateResponse<AgreementInstance>(instance, messages);
	}
	
	/**
	 * Create a site entry for the bill to UCN for this agreement.  NOT CURRENTLY USED.
	 * 
	 * @param instance
	 */
	private void autoCreateSite(AgreementInstance instance, boolean addSite) {
		if (!addSite)
			return;
		
		AgreementSite agreementSite = new AgreementSite();
		
		AgreementSiteId asId = new AgreementSiteId();
		asId.setAgreementId(instance.getId());
		asId.setSiteUcn(instance.getBillUcn());
		asId.setSiteUcnSuffix(instance.getBillUcnSuffix());
		if (asId.getSiteUcnSuffix() == 0)
			asId.setSiteUcnSuffix(1);
		asId.setSiteLocCode("");
		
		agreementSite.setId(asId);
		agreementSite.setCommissionCode("");
		agreementSite.setNote("Created automatically with agreement.");
		agreementSite.setOrgPath("");
		agreementSite.setCancelReasonCode("");
		agreementSite.setActiveDate(null);
		agreementSite.setInactiveDate(null);
		agreementSite.setCreatedDatetime(new Date());
		agreementSite.setStatus(AppConstants.STATUS_ACTIVE);
		
		DbAgreementSite.persist(agreementSite);
	}
	
	private void validateInput(AgreementInstance instance) throws IllegalArgumentException {
		AppAgreementValidator validator = new AppAgreementValidator();
		validator.setOriginal(instance);	//	This isn't really the original, but it's good enough, because it has the original ID
		testMessages(validator.validateAgreement(instance));
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
