package com.scholastic.sbam.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.CommissionType;
import com.scholastic.sbam.server.database.objects.DbCommissionType;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.validation.CodeValidator;
import com.scholastic.sbam.shared.validation.NameValidator;

public class AppCommissionTypeValidator {
	
	public static int MIN_CODE_LEN = 2;
	
	private List<String> messages = new ArrayList<String>();
	
	private	CommissionTypeInstance original;
	private CommissionType		 commissionType;

	public List<String> validateCommissionType(CommissionTypeInstance instance) {
		if (instance.getStatus() == 'X')
			return null;
		validateCommissionCode(instance.getCommissionCode(), instance.isNewRecord());
		validateDescription(instance.getDescription());
	//	validateActivate(instance.getActivate());
		validateStatus(instance.getStatus());
		return messages;
	}
	
	public List<String> validateCommissionCode(String value, boolean isNew) {
		if (isNew) {
			validateNewCommissionCode(value);
		} else {
			validateOldCommissionCode(value);
		}
		return messages;
	}
	
	public List<String> validateOldCommissionCode(String value) {
		if (!loadCommissionType())
			return messages;
		
		if (value == null || value.length() == 0) {
			addMessage("A commission type code is required.");
			return messages;
		}
		
		addMessage((new CodeValidator(MIN_CODE_LEN)).validate(value));
		
		if (!commissionType.getCommissionCode().equals(value))
			addMessage("Commission type code cannot be changed.");
		
		return messages;
	}
	
	public List<String> validateNewCommissionCode(String value) {
		addMessage((new CodeValidator(MIN_CODE_LEN)).validate(value));
		if (value != null && value.length() > 0) {
			CommissionType conflict = DbCommissionType.getByCode(value);
			if (conflict != null) {
				addMessage("Commission type code already exists.");
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
	
	private boolean loadCommissionType() {
		if (commissionType == null) {
			commissionType = DbCommissionType.getByCode(original.getCommissionCode());
			if (commissionType == null) {
				addMessage("Unexpected Error: Original commission type not found in database.");
				return false;
			}
		}
		return true;
	}
	
	private void addMessage(String message) {
		if (message != null && message.length() > 0)
			messages.add(message);
	}

	public CommissionTypeInstance getOriginal() {
		return original;
	}

	public void setOriginal(CommissionTypeInstance original) {
		this.original = original;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
