package com.scholastic.sbam.shared.validation;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AsyncValidationResponse implements IsSerializable {
	private int validationCounter;
	private List<String> messages		= new ArrayList<String>();
	private List<String> alertMessages;
	private List<String> infoMessages;
	
	public AsyncValidationResponse() {
	}
	
	public AsyncValidationResponse(int validationCounter) {
		setValidationCounter(validationCounter);
	}
	
	public AsyncValidationResponse(int validationCounter, String message) {
		setValidationCounter(validationCounter);
		addMessage(message);
	}
	
	public void addMessage(String message) {
		if (message != null && message.length() > 0)
			messages.add(message);
	}
	
	public void addMessages(List<String> messages) {
		if (messages != null && messages.size() > 0)
			for (String message: messages)
				addMessage(message);
	}
	
	public void addWarning(String message) {
		if (message != null && message.length() > 0) {
			if (alertMessages == null) alertMessages = new ArrayList<String>();
			alertMessages.add(message);
		}
	}
	
	public void addWarnings(List<String> messages) {
		if (messages != null && messages.size() > 0)
			for (String message: messages)
				addWarning(message);
	}
	
	public void addInfoMessage(String message) {
		if (message != null && message.length() > 0) {
			if (infoMessages == null) infoMessages = new ArrayList<String>();
			infoMessages.add(message);
		}
	}
	
	public void addInfoMessages(List<String> messages) {
		if (messages != null && messages.size() > 0)
			for (String message: messages)
				addInfoMessage(message);
	}

	public int getValidationCounter() {
		return validationCounter;
	}
	
	public void setValidationCounter(int validationCounter) {
		this.validationCounter = validationCounter;
	}
	
	public List<String> getMessages() {
		return messages;
	}
	
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	public List<String> getAlertMessages() {
		if (alertMessages == null) alertMessages = new ArrayList<String>();
		return alertMessages;
	}

	public void setWarningMessages(List<String> warningMessages) {
		this.alertMessages = warningMessages;
	}

	public List<String> getInfoMessages() {
		if (infoMessages == null) infoMessages = new ArrayList<String>();
		return infoMessages;
	}

	public void setInfoMessages(List<String> infoMessages) {
		this.infoMessages = infoMessages;
	}
	
}
