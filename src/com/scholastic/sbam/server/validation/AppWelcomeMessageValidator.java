package com.scholastic.sbam.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.WelcomeMessage;
import com.scholastic.sbam.server.database.objects.DbWelcomeMessage;
import com.scholastic.sbam.shared.objects.WelcomeMessageInstance;
import com.scholastic.sbam.shared.util.AppConstants;
import com.scholastic.sbam.shared.validation.NameValidator;

public class AppWelcomeMessageValidator {
	
	public static int MIN_CODE_LEN = 2;
	
	private List<String> messages = new ArrayList<String>();
	
	private	WelcomeMessageInstance original;
	private WelcomeMessage		 WelcomeMessage;

	public List<String> validateWelcomeMessage(WelcomeMessageInstance instance) {
		if (instance.getStatus() == 'X')
			return null;
		validateId(instance.getId(), instance.isNewRecord());
		validateTitle(instance.getTitle());
		validateContent(instance.getContent());
		validateStatus(instance.getStatus());
		return messages;
	}
	
	public List<String> validateId(int value, boolean isNew) {
		if (isNew) {
			validateNewWelcomeMessageId(value);	
		} else {
			validateOldWelcomeMessageId(value);
		}
		return messages;
	}
	
	public List<String> validateOldWelcomeMessageId(int value) {
		if (!loadWelcomeMessage())
			return messages;
		
		if (value < 0) {
			addMessage("A welcome message id is required.");
			return messages;
		}
		
		if (!WelcomeMessage.getId().equals(value))
			addMessage("Welcome message id cannot be changed.");
		
		return messages;
	}
	
	public List<String> validateNewWelcomeMessageId(int value) {
		if (value > 0) {
			WelcomeMessage conflict = DbWelcomeMessage.getById(value);
			if (conflict != null && conflict.getStatus() != AppConstants.STATUS_DELETED) {
				addMessage("Welcome message id already exists.");
			}
		}
		return messages;
	}
	
	public List<String> validateTitle(String title) {
		addMessage(new NameValidator("title", 10, 1000).validate(title));
		return messages;
	}
	
	public List<String> validateContent(String content) {
		if (content == null || content.length() < 10)
			addMessage("Please specify a message at least ten characters long.");
//		addMessage(new NameValidator("message", 10, 4000).validate(content));
		return messages;
	}
	
	public List<String> validateStatus(char status) {
		if (status != 0 && status != 'A' && status != 'I' && status != 'X')
			addMessage("Invalid status " + status);
		return messages;
	}
	
	private boolean loadWelcomeMessage() {
		if (WelcomeMessage == null) {
			WelcomeMessage = DbWelcomeMessage.getById(original.getId());
			if (WelcomeMessage == null) {
				addMessage("Unexpected Error: Original welcome message not found in database.");
				return false;
			}
		}
		return true;
	}
	
	private void addMessage(String message) {
		if (message != null && message.length() > 0)
			messages.add(message);
	}

	public WelcomeMessageInstance getOriginal() {
		return original;
	}

	public void setOriginal(WelcomeMessageInstance original) {
		this.original = original;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
