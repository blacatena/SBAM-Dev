package com.scholastic.sbam.server.validation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.AgreementTerm;
import com.scholastic.sbam.server.database.codegen.TermType;
import com.scholastic.sbam.server.database.codegen.CommissionType;
import com.scholastic.sbam.server.database.codegen.CancelReason;
import com.scholastic.sbam.server.database.objects.DbAgreementTerm;
import com.scholastic.sbam.server.database.objects.DbTermType;
import com.scholastic.sbam.server.database.objects.DbCommissionType;
import com.scholastic.sbam.server.database.objects.DbCancelReason;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;
import com.scholastic.sbam.shared.util.AppConstants;
import com.scholastic.sbam.shared.validation.NameValidator;

public class AppAgreementTermValidator {
	
	public static int MIN_CODE_LEN = 2;
	
	private List<String> messages = new ArrayList<String>();
	
	private	AgreementTermInstance original;
	private AgreementTerm		 agreementTerm;

	public List<String> validateAgreementTerm(AgreementTermInstance instance) {
		if (instance.getStatus() == AppConstants.STATUS_DELETED)
			return null;
		if (instance.getStatus() == AppConstants.STATUS_ANY_NONE)
			instance.setStatus(AppConstants.STATUS_ACTIVE);
		validateCancelReasonCode(instance.getCancelReasonCode(), instance); // This may change the status to I, set cancel date
		validateAgreementTermId(instance.getAgreementId(), instance.getId(), instance.isNewRecord());
		validateTermType(instance.getTermTypeCode(), instance);
		validateCommissionCode(instance.getCommissionCode(), instance);
		validateStatus(instance.getStatus());
		if (instance.getDollarValue() < 0)
			addMessage("Dollar value may not be negative.");
		return messages;
	}
	
	public List<String> validateAgreementTermId(int agreementId, int termId, boolean isNew) {
		if (isNew) {
			validateNewAgreementTermId(agreementId, termId);
		} else {
			validateOldAgreementTermId(agreementId, termId);
		}
		return messages;
	}
	
	public List<String> validateOldAgreementTermId(int agreementId, int termId) {
		if (!loadAgreementTerm())
			return messages;
		
		if (agreementId <= 0) {
			addMessage("An agreement ID is required.");
			return messages;
		}
		
		if (termId <= 0) {
			addMessage("A term ID is required.");
			return messages;
		}
		
		if (agreementTerm.getId().getAgreementId() != agreementId)
			addMessage("Agreement ID cannot be changed.");
		
		if (agreementTerm.getId().getTermId() != termId)
			addMessage("Term ID cannot be changed.");
		
		return messages;
	}
	
	public List<String> validateNewAgreementTermId(int agreementId, int termId) {
		if (agreementId > 0 && termId > 0) {
			AgreementTerm conflict = DbAgreementTerm.getById(agreementId, termId);
			if (conflict != null) {
				addMessage("Agreement Term already exists.");
			}
		}
		return messages;
	}
	
	public List<String> validateDescription(String description) {
		addMessage(new NameValidator().validate(description));
		return messages;
	}
	
	public List<String> validateShortName(String shortName) {
		addMessage(new NameValidator("short name").validate(shortName));
		return messages;
	}
	
	public List<String> validateTermType(String agreementType, AgreementTermInstance instance) {
		if (agreementType == null || agreementType.length() == 0)
			addMessage("A term type is required.");
		else {
			TermType aType = DbTermType.getByCode(agreementType);
			if (aType == null)
				addMessage("Term type '" + agreementType + "' not found in the database.");
			else
				instance.setTermType(DbTermType.getInstance(aType));
		}
		return messages;
	}
	
	public List<String> validateCommissionCode(String commissionCode, AgreementTermInstance instance) {
		if (commissionCode == null || commissionCode.length() == 0)
			return messages;
		else {
			CommissionType cType = DbCommissionType.getByCode(commissionCode);
			if (cType == null)
				addMessage("Commission type code '" + commissionCode + "' not found in the database.");
			else
				instance.setCommissionType(DbCommissionType.getInstance(cType));
		}
		return messages;
	}
	
	public List<String> validateCancelReasonCode(String cancelReasonCode, AgreementTermInstance instance) {
		if (cancelReasonCode == null || cancelReasonCode.length() == 0)
			return messages;

		CancelReason cReason = DbCancelReason.getByCode(cancelReasonCode);
		if (cReason == null)
			addMessage("Cancel reason code '" + cancelReasonCode + "' not found in the database.");
		else {
			instance.setCancelReason(DbCancelReason.getInstance(cReason));
			if (instance.getStatus() != AppConstants.STATUS_DELETED)
				instance.setStatus(AppConstants.STATUS_INACTIVE);
			if (instance.getCancelDate() == null)
				instance.setCancelDate(new Date());
		}
		
		return messages;
	}
	
	public List<String> validateStatus(char status) {
		if (status != AppConstants.STATUS_ACTIVE && status != AppConstants.STATUS_INACTIVE && status != AppConstants.STATUS_EXPIRED && status != AppConstants.STATUS_DELETED)
			addMessage("Invalid status " + status);
		return messages;
	}
	
	private boolean loadAgreementTerm() {
		if (agreementTerm == null) {
			agreementTerm = DbAgreementTerm.getById(original.getAgreementId(), original.getId());
			if (agreementTerm == null) {
				addMessage("Unexpected Error: Original agreement term not found in the database.");
				return false;
			}
		}
		return true;
	}
	
	private void addMessage(String message) {
		if (message != null && message.length() > 0)
			messages.add(message);
	}

	public AgreementTermInstance getOriginal() {
		return original;
	}

	public void setOriginal(AgreementTermInstance original) {
		this.original = original;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
