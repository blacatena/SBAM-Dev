package com.scholastic.sbam.shared.objects;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UpdateResponse<I extends BetterRowEditInstance> implements IsSerializable {
	private String message;
	private I instance;
	
	public UpdateResponse() {
		
	}
	
	public UpdateResponse(I instance) {
		this.instance = instance;
	}
	
	public UpdateResponse(I instance, String message) {
		this.instance	=	instance;
		this.message	=	message;
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
}
