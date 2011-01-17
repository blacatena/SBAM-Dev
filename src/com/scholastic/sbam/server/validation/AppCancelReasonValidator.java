package com.scholastic.sbam.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.CancelReason;
import com.scholastic.sbam.server.database.objects.DbCancelReason;
import com.scholastic.sbam.shared.objects.CancelReasonInstance;
import com.scholastic.sbam.shared.validation.CodeValidator;
import com.scholastic.sbam.shared.validation.NameValidator;

public class AppCancelReasonValidator {
	
	private List<String> messages = new ArrayList<String>();
	
	private	CancelReasonInstance original;
	private CancelReason		 cancelReason;

	public List<String> validateCancelReason(CancelReasonInstance instance) {
		if (instance.getStatus() == 'X')
			return null;
		validateCancelReasonCode(instance.getCancelReasonCode());
		validateDescription(instance.getDescription());
	//	validateChangeNotCancel(instance.getChangeNotCancel());
		validateStatus(instance.getStatus());
		return messages;
	}
	
	public List<String> validateCancelReasonCode(String value) {
		if (original.getCancelReasonCode() != null && original.getCancelReasonCode().length() > 0) {
			validateOldCancelReasonCode(value);
		} else {
			validateNewCancelReasonCode(value);
		}
		return messages;
	}
	
	public List<String> validateOldCancelReasonCode(String value) {
		if (!loadCancelReason())
			return messages;
		
		if (value == null || value.length() == 0) {
			addMessage("A cancel reason code is required.");
			return messages;
		}
		
		addMessage((new CodeValidator(2)).validate(value));
		
		if (!cancelReason.getCancelReasonCode().equals(value))
			addMessage("Cancel reason code cannot be changed.");
		
		return messages;
	}
	
	public List<String> validateNewCancelReasonCode(String value) {
		addMessage((new CodeValidator()).validate(value));
		if (value != null && value.length() > 0) {
			CancelReason conflict = DbCancelReason.getByCode(value);
			if (conflict != null) {
				addMessage("Cancel reason code already exists.");
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
	
	public List<String> validateChangeNotCancel(char changeNotCancel) {
		if (changeNotCancel != 0 && changeNotCancel != 'n' && changeNotCancel != 'y')
			addMessage("Invalid change-not-cancel " + changeNotCancel);
		return messages;
	}
	
	private boolean loadCancelReason() {
		if (cancelReason == null) {
			cancelReason = DbCancelReason.getByCode(original.getCancelReasonCode());
			if (cancelReason == null) {
				addMessage("Unexpected Error: Original cancel reason not found in database.");
				return false;
			}
		}
		return true;
	}
	
	private void addMessage(String message) {
		if (message != null && message.length() > 0)
			messages.add(message);
	}

	public CancelReasonInstance getOriginal() {
		return original;
	}

	public void setOriginal(CancelReasonInstance original) {
		this.original = original;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
