package com.scholastic.sbam.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.AgreementType;
import com.scholastic.sbam.server.database.codegen.CommissionType;
import com.scholastic.sbam.server.database.codegen.DeleteReason;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.objects.DbAgreement;
import com.scholastic.sbam.server.database.objects.DbAgreementType;
import com.scholastic.sbam.server.database.objects.DbCommissionType;
import com.scholastic.sbam.server.database.objects.DbDeleteReason;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.util.AppConstants;
import com.scholastic.sbam.shared.validation.NameValidator;

public class AppAgreementValidator {
	
	public static int MIN_CODE_LEN = 2;
	
	private List<String> messages = new ArrayList<String>();
	
	private	AgreementInstance original;
	private Agreement		 agreement;

	public List<String> validateAgreement(AgreementInstance instance) {
		if (instance.getStatus() == AppConstants.STATUS_DELETED)
			return null;
		if (instance.getStatus() == AppConstants.STATUS_ANY_NONE)
			instance.setStatus(AppConstants.STATUS_ACTIVE);
		validateDeleteReasonCode(instance.getDeleteReasonCode(), instance); // This may change the status to X
		validateAgreementId(instance.getId(), instance.isNewRecord());
		validateAgreementType(instance.getAgreementTypeCode(), instance);
		validateCommissionCode(instance.getCommissionCode(), instance);
		validateBillUcn(instance.getBillUcn(), instance);
		validateBillUcnSuffix(instance.getBillUcnSuffix(), instance);	// This may change the UCN Suffix to 1
		validateStatus(instance.getStatus());
		return messages;
	}
	
	public List<String> validateAgreementId(int value, boolean isNew) {
		if (isNew) {
			validateNewAgreementId(value);
		} else {
			validateOldAgreementId(value);
		}
		return messages;
	}
	
	public List<String> validateOldAgreementId(int value) {
		if (!loadAgreement())
			return messages;
		
		if (value <= 0) {
			addMessage("An agreement ID is required.");
			return messages;
		}
		
		if (!agreement.getId().equals(value))
			addMessage("Agreement ID cannot be changed.");
		
		return messages;
	}
	
	public List<String> validateNewAgreementId(int value) {
		if (value > 0) {
			Agreement conflict = DbAgreement.getById(value);
			if (conflict != null) {
				addMessage("Agreement ID already exists.");
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
	
	public List<String> validateAgreementType(String agreementType, AgreementInstance instance) {
		if (agreementType == null || agreementType.length() == 0)
			addMessage("An agreement type is required.");
		else {
			AgreementType aType = DbAgreementType.getByCode(agreementType);
			if (aType == null)
				addMessage("Agreement type '" + agreementType + "' not found in the database.");
			else
				instance.setAgreementType(DbAgreementType.getInstance(aType));
		}
		return messages;
	}
	
	public List<String> validateCommissionCode(String commissionCode, AgreementInstance instance) {
		if (commissionCode == null || commissionCode.length() == 0)
			addMessage("A commission type code is required.");
		else {
			CommissionType cType = DbCommissionType.getByCode(commissionCode);
			if (cType == null)
				addMessage("Commission type code '" + commissionCode + "' not found in the database.");
			else
				instance.setCommissionType(DbCommissionType.getInstance(cType));
		}
		return messages;
	}
	
	public List<String> validateDeleteReasonCode(String deleteReasonCode, AgreementInstance instance) {
		if (deleteReasonCode == null || deleteReasonCode.length() == 0)
			return messages;

		DeleteReason dReason = DbDeleteReason.getByCode(deleteReasonCode);
		if (dReason == null)
			addMessage("Delete reason code '" + deleteReasonCode + "' not found in the database.");
		else {
			instance.setDeleteReason(DbDeleteReason.getInstance(dReason));
			instance.setStatus(AppConstants.STATUS_DELETED);
		}
		
		return messages;
	}
	
	public List<String> validateBillUcn(int billUcn, AgreementInstance instance) {
		if (billUcn <= 0)
			addMessage("A Bill To UCN is required.");
		else {
			Institution inst = DbInstitution.getByCode(billUcn);
			if (inst == null)
				addMessage("Bill To UCN '" + billUcn + "' not found in the database.");
			else
				instance.setInstitution(DbInstitution.getInstance(inst));
		}
		return messages;
	}
	
	public List<String> validateBillUcnSuffix(int billUcnSuffix, AgreementInstance instance) {
		//	For new records, always set the UCN Suffix to 1
		if (instance.isNewRecord())
			instance.setBillUcnSuffix(1);
		//	For old records, if the UCN changes, reset the UCN suffix to 1
		else if (agreement != null)
			if (instance.getBillUcn() != agreement.getBillUcn())
				instance.setBillUcnSuffix(1);
		return messages;
	}
	
	public List<String> validateStatus(char status) {
		if (status != AppConstants.STATUS_ACTIVE && status != AppConstants.STATUS_INACTIVE && status != AppConstants.STATUS_EXPIRED && status != AppConstants.STATUS_DELETED)
			addMessage("Invalid status " + status);
		return messages;
	}
	
	private boolean loadAgreement() {
		if (agreement == null) {
			agreement = DbAgreement.getById(original.getId());
			if (agreement == null) {
				addMessage("Unexpected Error: Original agreement ID not found in the database.");
				return false;
			}
		}
		return true;
	}
	
	private void addMessage(String message) {
		if (message != null && message.length() > 0)
			messages.add(message);
	}

	public AgreementInstance getOriginal() {
		return original;
	}

	public void setOriginal(AgreementInstance original) {
		this.original = original;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
