package com.scholastic.sbam.shared.validation;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AsyncValidationResponse implements IsSerializable {
	private int validationCounter;
	private List<String> messages = new ArrayList<String>();
	
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
	
}
