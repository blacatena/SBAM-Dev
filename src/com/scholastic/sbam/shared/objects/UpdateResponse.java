package com.scholastic.sbam.shared.objects;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UpdateResponse<I extends BetterRowEditInstance> implements IsSerializable {
	private String message;
	private I instance;
	private boolean newCreated;
	
	public UpdateResponse() {
		
	}
	
	public UpdateResponse(I instance) {
		this.instance = instance;
	}
	
	public UpdateResponse(I instance, String message) {
		this.instance	=	instance;
		this.message	=	message;
	}
	
	public UpdateResponse(I instance, String message, boolean newCreated) {
		this.instance	=	instance;
		this.message	=	message;
		this.newCreated	=	newCreated;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public I getInstance() {
		return instance;
	}
	
	public void setInstance(I instance) {
		this.instance = instance;
	}

	public boolean isNewCreated() {
		return newCreated;
	}

	public void setNewCreated(boolean newCreated) {
		this.newCreated = newCreated;
	}
	
}
