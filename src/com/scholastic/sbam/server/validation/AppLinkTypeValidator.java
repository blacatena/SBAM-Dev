package com.scholastic.sbam.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.LinkType;
import com.scholastic.sbam.server.database.objects.DbLinkType;
import com.scholastic.sbam.shared.objects.LinkTypeInstance;
import com.scholastic.sbam.shared.validation.CodeValidator;
import com.scholastic.sbam.shared.validation.NameValidator;

public class AppLinkTypeValidator {
	
	public static int MIN_CODE_LEN = 2;
	
	private List<String> messages = new ArrayList<String>();
	
	private	LinkTypeInstance original;
	private LinkType		 linkType;

	public List<String> validateLinkType(LinkTypeInstance instance) {
		if (instance.getStatus() == 'X')
			return null;
		validateLinkTypeCode(instance.getLinkTypeCode(), instance.isNewRecord());
		validateDescription(instance.getDescription());
	//	validateActivate(instance.getActivate());
		validateStatus(instance.getStatus());
		return messages;
	}
	
	public List<String> validateLinkTypeCode(String value, boolean isNew) {
		if (isNew) {
			validateNewLinkTypeCode(value);	
		} else {
			validateOldLinkTypeCode(value);
		}
		return messages;
	}
	
	public List<String> validateOldLinkTypeCode(String value) {
		if (!loadLinkType())
			return messages;
		
		if (value == null || value.length() == 0) {
			addMessage("A link type code is required.");
			return messages;
		}
		
		addMessage((new CodeValidator(MIN_CODE_LEN)).validate(value));
		
		if (!linkType.getLinkTypeCode().equals(value))
			addMessage("Link type code cannot be changed.");
		
		return messages;
	}
	
	public List<String> validateNewLinkTypeCode(String value) {
		addMessage((new CodeValidator(MIN_CODE_LEN)).validate(value));
		if (value != null && value.length() > 0) {
			LinkType conflict = DbLinkType.getByCode(value);
			if (conflict != null) {
				addMessage("Link type code already exists.");
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
	
	private boolean loadLinkType() {
		if (linkType == null) {
			linkType = DbLinkType.getByCode(original.getLinkTypeCode());
			if (linkType == null) {
				addMessage("Unexpected Error: Original link type not found in database.");
				return false;
			}
		}
		return true;
	}
	
	private void addMessage(String message) {
		if (message != null && message.length() > 0)
			messages.add(message);
	}

	public LinkTypeInstance getOriginal() {
		return original;
	}

	public void setOriginal(LinkTypeInstance original) {
		this.original = original;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
