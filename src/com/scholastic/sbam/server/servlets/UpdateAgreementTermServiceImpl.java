package com.scholastic.sbam.server.servlets;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.scholastic.sbam.client.services.UpdateAgreementTermService;
import com.scholastic.sbam.server.database.codegen.AgreementTerm;
import com.scholastic.sbam.server.database.codegen.AgreementTermId;
import com.scholastic.sbam.server.database.objects.DbAgreementTerm;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.validation.AppAgreementTermValidator;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateAgreementTermServiceImpl extends AuthenticatedServiceServlet implements UpdateAgreementTermService {

	@Override
	public UpdateResponse<AgreementTermInstance> updateAgreementTerm(AgreementTermInstance instance) throws IllegalArgumentException {
		
		boolean newCreated				= false;
		
		String	messages				= null;
		
		AgreementTerm dbInstance = null;
		
		authenticate("update agreement term", SecurityManager.ROLE_MAINT);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Pre-edit/fix values
			validateInput(instance);
			
			//	Get existing, or create new
			if (instance.getTermId() > 0) {
				dbInstance = DbAgreementTerm.getById(instance.getAgreementId(), instance.getTermId());
			}

			//	If none found, create new
			if (dbInstance == null) {
				newCreated = true;
				dbInstance = new AgreementTerm();
				AgreementTermId id = new AgreementTermId();
				id.setAgreementId(instance.getAgreementId());
				id.setTermId(DbAgreementTerm.getNextTermId(instance.getAgreementId()));
				dbInstance.setId(id);
				//	Set the create date/time and status
				dbInstance.setCreatedDatetime(new Date());
			}

			//	Update values
			
			if (instance.getStatus() != 0)
				dbInstance.setStatus(instance.getStatus());
			if (instance.getProductCode() != null)
				dbInstance.setProductCode(instance.getProductCode());
			if (instance.getTermTypeCode() != null)
				dbInstance.setTermType(instance.getTermTypeCode());
			if (instance.getCommissionCode() != null)
				dbInstance.setCommissionCode(instance.getCommissionCode());
			if (instance.getCancelReasonCode() != null)
				dbInstance.setCancelReasonCode(instance.getCancelReasonCode());
			if (instance.getOrgPath() != null)
				dbInstance.setOrgPath(instance.getOrgPath());
			if (instance.getPrimaryOrgPath() != null)
				dbInstance.setPrimaryOrgPath(instance.getPrimaryOrgPath());
			if (instance.getPrimary() != 0)
				dbInstance.setPrimaryTerm(instance.getPrimary());
			if (instance.getNote() != null)
				dbInstance.setNote(instance.getNote());

			if (instance.getCancelDate() != null)
				dbInstance.setCancelDate(instance.getCancelDate());
			if (instance.getStartDate() != null)
				dbInstance.setStartDate(instance.getStartDate());
			if (instance.getEndDate() != null)
				dbInstance.setEndDate(instance.getEndDate());
			if (instance.getTerminateDate() != null)
				dbInstance.setTerminateDate(instance.getTerminateDate());

			if (instance.getDollarValue() >= 0)
				dbInstance.setDollarValue(new BigDecimal(instance.getDollarValue()));
			
			if (instance.getBuildings() >= 0)
				dbInstance.setBuildings(instance.getBuildings());
			if (instance.getEnrollment() >= 0)
				dbInstance.setEnrollment(instance.getEnrollment());
			if (instance.getPopulation() >= 0)
				dbInstance.setPopulation(instance.getPopulation());
			if (instance.getWorkstations() >= 0)
				dbInstance.setWorkstations(instance.getWorkstations());
			
			if (instance.getPoNumber() != null)
				dbInstance.setPoNumber(instance.getPoNumber());
			if (instance.getReferenceSaId() >= 0)
				dbInstance.setReferenceSaId(instance.getReferenceSaId());
				
			//	Fix any nulls
			if (dbInstance.getCancelReasonCode() == null)
				dbInstance.setCancelReasonCode("");
			if (dbInstance.getOrgPath() == null)
				dbInstance.setOrgPath("");
			if (dbInstance.getPrimaryOrgPath() == null)
				dbInstance.setPrimaryOrgPath(dbInstance.getOrgPath());
			if (dbInstance.getNote() == null)
				dbInstance.setNote("");
			if (dbInstance.getPoNumber() == null)
				dbInstance.setPoNumber("");
			
			//	Fix cancel values
			if (dbInstance.getCancelReasonCode() != null || dbInstance.getCancelReasonCode().length() > 0) {
				if (dbInstance.getCancelDate() == null)
					dbInstance.setCancelDate(new Date());
				if (dbInstance.getStatus() != AppConstants.STATUS_DELETED)	// If it's already marked DELETED, then that's better than cancelled, so leave it
					dbInstance.setStatus(AppConstants.STATUS_INACTIVE);
			} else {
				if (dbInstance.getCancelDate() != null)
					dbInstance.setCancelDate(null);
				if (dbInstance.getStatus() == AppConstants.STATUS_INACTIVE)
					dbInstance.setStatus(AppConstants.STATUS_ACTIVE);
			}
			
			//	Persist in database
			DbAgreementTerm.persist(dbInstance);
			
			//	Refresh when new row is created, to get assigned ID
			if (newCreated) {
			//	DbAgreementTerm.refresh(dbInstance);	// This may not be necessary, but just in case
				instance.setTermId(dbInstance.getId().getTermId());
				instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
				DbAgreementTerm.setDescriptions(instance);
			}
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The agreement term update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return new UpdateResponse<AgreementTermInstance>(instance, messages);
	}
	
	private void validateInput(AgreementTermInstance instance) throws IllegalArgumentException {
		AppAgreementTermValidator validator = new AppAgreementTermValidator();
		validator.setOriginal(instance);	//	This isn't really the original, but it's good enough, because it has the original ID
		testMessages(validator.validateAgreementTerm(instance));
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
