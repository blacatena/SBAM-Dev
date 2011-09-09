package com.scholastic.sbam.shared.validation;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AsyncValidationResponse implements IsSerializable {
	protected int validationCounter;
	protected List<String> errorMessages		= new ArrayList<String>();
	protected List<String> alertMessages;
	protected List<String> infoMessages;
	
	public AsyncValidationResponse() {
	}
	
	public AsyncValidationResponse(int validationCounter) {
		setValidationCounter(validationCounter);
	}
	
	public AsyncValidationResponse(int validationCounter, String message) {
		setValidationCounter(validationCounter);
		addErrorMessage(message);
	}
	
	public void addErrorMessage(String message) {
		if (message != null && message.length() > 0)
			errorMessages.add(message);
	}
	
	public void addErrorMessages(List<String> messages) {
		if (messages != null && messages.size() > 0)
			for (String message: messages)
				addErrorMessage(message);
	}
	
	public void addAlertMessage(String message) {
		if (message != null && message.length() > 0) {
			if (alertMessages == null) alertMessages = new ArrayList<String>();
			alertMessages.add(message);
		}
	}
	
	public void addAlertMessages(List<String> messages) {
		if (messages != null && messages.size() > 0)
			for (String message: messages)
				addAlertMessage(message);
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
	
	public List<String> getErrorMessages() {
		return errorMessages;
	}
	
	public void setErrorMessages(List<String> messages) {
		this.errorMessages = messages;
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
