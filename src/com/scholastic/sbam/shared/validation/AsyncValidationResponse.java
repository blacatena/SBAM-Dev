package com.scholastic.sbam.shared.validation;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AsyncValidationResponse implements IsSerializable {
	private int validationCounter;
	private String message;
	
	public AsyncValidationResponse() {
	}
	
	public AsyncValidationResponse(int validationCounter) {
		setValidationCounter(validationCounter);
	}
	
	public AsyncValidationResponse(int validationCounter, String message) {
		setValidationCounter(validationCounter);
		setMessage(message);
	}

	public int getValidationCounter() {
		return validationCounter;
	}
	public void setValidationCounter(int validationCounter) {
		this.validationCounter = validationCounter;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
