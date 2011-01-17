package com.scholastic.sbam.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.DeleteReason;
import com.scholastic.sbam.server.database.objects.DbDeleteReason;
import com.scholastic.sbam.shared.objects.DeleteReasonInstance;
import com.scholastic.sbam.shared.validation.CodeValidator;
import com.scholastic.sbam.shared.validation.NameValidator;

public class AppDeleteReasonValidator {
	
	private List<String> messages = new ArrayList<String>();
	
	private	DeleteReasonInstance original;
	private DeleteReason		 deleteReason;

	public List<String> validateDeleteReason(DeleteReasonInstance instance) {
		if (instance.getStatus() == 'X')
			return null;
		validateDeleteReasonCode(instance.getDeleteReasonCode());
		validateDescription(instance.getDescription());
		validateStatus(instance.getStatus());
		return messages;
	}
	
	public List<String> validateDeleteReasonCode(String value) {
		if (original.getDeleteReasonCode() != null && original.getDeleteReasonCode().length() > 0) {
			validateOldDeleteReasonCode(value);
		} else {
			validateNewDeleteReasonCode(value);
		}
		return messages;
	}
	
	public List<String> validateOldDeleteReasonCode(String value) {
		if (!loadDeleteReason())
			return messages;
		
		if (value == null || value.length() == 0) {
			addMessage("A delete reason code is required.");
			return messages;
		}
		
		addMessage((new CodeValidator(2)).validate(value));
		
		if (!deleteReason.getDeleteReasonCode().equals(value))
			addMessage("Delete reason code cannot be changed.");
		
		return messages;
	}
	
	public List<String> validateNewDeleteReasonCode(String value) {
		addMessage((new CodeValidator()).validate(value));
		if (value != null && value.length() > 0) {
			DeleteReason conflict = DbDeleteReason.getByCode(value);
			if (conflict != null) {
				addMessage("Delete reason code already exists.");
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
	
	private boolean loadDeleteReason() {
		if (deleteReason == null) {
			deleteReason = DbDeleteReason.getByCode(original.getDeleteReasonCode());
			if (deleteReason == null) {
				addMessage("Unexpected Error: Original delete reason not found in database.");
				return false;
			}
		}
		return true;
	}
	
	private void addMessage(String message) {
		if (message != null && message.length() > 0)
			messages.add(message);
	}

	public DeleteReasonInstance getOriginal() {
		return original;
	}

	public void setOriginal(DeleteReasonInstance original) {
		this.original = original;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
