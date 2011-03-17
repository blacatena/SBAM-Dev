package com.scholastic.sbam.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.ContactType;
import com.scholastic.sbam.server.database.objects.DbContactType;
import com.scholastic.sbam.shared.objects.ContactTypeInstance;
import com.scholastic.sbam.shared.validation.CodeValidator;
import com.scholastic.sbam.shared.validation.NameValidator;

public class AppContactTypeValidator {
	
	public static int MIN_CODE_LEN = 2;
	
	private List<String> messages = new ArrayList<String>();
	
	private	ContactTypeInstance original;
	private ContactType		 contactType;

	public List<String> validateContactType(ContactTypeInstance instance) {
		if (instance.getStatus() == 'X')
			return null;
		validateContactTypeCode(instance.getContactTypeCode(), instance.isNewRecord());
		validateDescription(instance.getDescription());
	//	validateActivate(instance.getActivate());
		validateStatus(instance.getStatus());
		return messages;
	}
	
	public List<String> validateContactTypeCode(String value, boolean isNew) {
		if (isNew) {
			validateNewContactTypeCode(value);	
		} else {
			validateOldContactTypeCode(value);
		}
		return messages;
	}
	
	public List<String> validateOldContactTypeCode(String value) {
		if (!loadContactType())
			return messages;
		
		if (value == null || value.length() == 0) {
			addMessage("A contact type code is required.");
			return messages;
		}
		
		addMessage((new CodeValidator(MIN_CODE_LEN)).validate(value));
		
		if (!contactType.getContactTypeCode().equals(value))
			addMessage("Contact type code cannot be changed.");
		
		return messages;
	}
	
	public List<String> validateNewContactTypeCode(String value) {
		addMessage((new CodeValidator(MIN_CODE_LEN)).validate(value));
		if (value != null && value.length() > 0) {
			ContactType conflict = DbContactType.getByCode(value);
			if (conflict != null) {
				addMessage("Contact type code already exists.");
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
	
	private boolean loadContactType() {
		if (contactType == null) {
			contactType = DbContactType.getByCode(original.getContactTypeCode());
			if (contactType == null) {
				addMessage("Unexpected Error: Original contact type not found in database.");
				return false;
			}
		}
		return true;
	}
	
	private void addMessage(String message) {
		if (message != null && message.length() > 0)
			messages.add(message);
	}

	public ContactTypeInstance getOriginal() {
		return original;
	}

	public void setOriginal(ContactTypeInstance original) {
		this.original = original;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
