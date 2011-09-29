package com.scholastic.sbam.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.TermType;
import com.scholastic.sbam.server.database.objects.DbTermType;
import com.scholastic.sbam.shared.objects.TermTypeInstance;
import com.scholastic.sbam.shared.util.AppConstants;
import com.scholastic.sbam.shared.validation.CodeValidator;
import com.scholastic.sbam.shared.validation.NameValidator;

public class AppTermTypeValidator {
	
	public static int MIN_CODE_LEN = 2;
	
	private List<String> messages = new ArrayList<String>();
	
	private	TermTypeInstance original;
	private TermType		 termType;

	public List<String> validateTermType(TermTypeInstance instance) {
		if (instance.getStatus() == 'X')
			return null;
		validateTermTypeCode(instance.getTermTypeCode(), instance.isNewRecord());
		validateDescription(instance.getDescription());
	//	validateActivate(instance.getActivate());
		validateStatus(instance.getStatus());
		return messages;
	}
	
	public List<String> validateTermTypeCode(String value, boolean isNew) {
		if (isNew) {
			validateNewTermTypeCode(value);	
		} else {
			validateOldTermTypeCode(value);
		}
		return messages;
	}
	
	public List<String> validateOldTermTypeCode(String value) {
		if (!loadTermType())
			return messages;
		
		if (value == null || value.length() == 0) {
			addMessage("A term type code is required.");
			return messages;
		}
		
		addMessage((new CodeValidator(MIN_CODE_LEN)).validate(value));
		
		if (!termType.getTermTypeCode().equals(value))
			addMessage("Term type code cannot be changed.");
		
		return messages;
	}
	
	public List<String> validateNewTermTypeCode(String value) {
		addMessage((new CodeValidator(MIN_CODE_LEN)).validate(value));
		if (value != null && value.length() > 0) {
			TermType conflict = DbTermType.getByCode(value);
			if (conflict != null && conflict.getStatus() != AppConstants.STATUS_DELETED) {
				addMessage("Term type code already exists.");
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
	
	private boolean loadTermType() {
		if (termType == null) {
			termType = DbTermType.getByCode(original.getTermTypeCode());
			if (termType == null) {
				addMessage("Unexpected Error: Original term type not found in database.");
				return false;
			}
		}
		return true;
	}
	
	private void addMessage(String message) {
		if (message != null && message.length() > 0)
			messages.add(message);
	}

	public TermTypeInstance getOriginal() {
		return original;
	}

	public void setOriginal(TermTypeInstance original) {
		this.original = original;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
