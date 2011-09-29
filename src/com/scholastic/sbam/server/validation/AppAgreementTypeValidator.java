package com.scholastic.sbam.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.AgreementType;
import com.scholastic.sbam.server.database.objects.DbAgreementType;
import com.scholastic.sbam.shared.objects.AgreementTypeInstance;
import com.scholastic.sbam.shared.util.AppConstants;
import com.scholastic.sbam.shared.validation.CodeValidator;
import com.scholastic.sbam.shared.validation.NameValidator;

public class AppAgreementTypeValidator {
	
	public static int MIN_CODE_LEN = 2;
	
	private List<String> messages = new ArrayList<String>();
	
	private	AgreementTypeInstance original;
	private AgreementType		 agreementType;

	public List<String> validateAgreementType(AgreementTypeInstance instance) {
		if (instance.getStatus() == 'X')
			return null;
		validateAgreementTypeCode(instance.getAgreementTypeCode(), instance.isNewRecord());
		validateDescription(instance.getDescription());
	//	validateActivate(instance.getActivate());
		validateStatus(instance.getStatus());
		return messages;
	}
	
	public List<String> validateAgreementTypeCode(String value, boolean isNew) {
		if (isNew) {
			validateNewAgreementTypeCode(value);	
		} else {
			validateOldAgreementTypeCode(value);
		}
		return messages;
	}
	
	public List<String> validateOldAgreementTypeCode(String value) {
		if (!loadAgreementType())
			return messages;
		
		if (value == null || value.length() == 0) {
			addMessage("A agreement type code is required.");
			return messages;
		}
		
		addMessage((new CodeValidator(MIN_CODE_LEN)).validate(value));
		
		if (!agreementType.getAgreementTypeCode().equals(value))
			addMessage("Agreement type code cannot be changed.");
		
		return messages;
	}
	
	public List<String> validateNewAgreementTypeCode(String value) {
		addMessage((new CodeValidator(MIN_CODE_LEN)).validate(value));
		if (value != null && value.length() > 0) {
			AgreementType conflict = DbAgreementType.getByCode(value);
			if (conflict != null && conflict.getStatus() != AppConstants.STATUS_DELETED) {
				addMessage("Agreement type code already exists.");
			}
		}
		return messages;
	}
	
	public List<String> validateDescription(String description) {
		addMessage(new NameValidator().validate(description));
		return messages;
	}
	
	public List<String> validateStatus(char status) {
		if (status != 0 && status != 'A' && status != 'I' && status != 'X')
			addMessage("Invalid status " + status);
		return messages;
	}
	
	public List<String> validateActivate(char activate) {
		if (activate != 0 && activate != 'n' && activate != 'y')
			addMessage("Invalid activation flag " + activate);
		return messages;
	}
	
	private boolean loadAgreementType() {
		if (agreementType == null) {
			agreementType = DbAgreementType.getByCode(original.getAgreementTypeCode());
			if (agreementType == null) {
				addMessage("Unexpected Error: Original agreement type not found in database.");
				return false;
			}
		}
		return true;
	}
	
	private void addMessage(String message) {
		if (message != null && message.length() > 0)
			messages.add(message);
	}

	public AgreementTypeInstance getOriginal() {
		return original;
	}

	public void setOriginal(AgreementTypeInstance original) {
		this.original = original;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
