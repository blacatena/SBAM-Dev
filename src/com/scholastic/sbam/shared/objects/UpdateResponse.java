package com.scholastic.sbam.shared.objects;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UpdateResponse<I extends BetterRowEditInstance> implements IsSerializable {
	private String message;
	private I instance;
	private boolean newCreated;
	private HashMap<String, Object> otherInfo;
	
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

	public HashMap<String, Object> getOtherInfo() {
		if (otherInfo == null)
			otherInfo = new HashMap<String, Object>();
		return otherInfo;
	}

	public void setOtherInfo(HashMap<String, Object> otherInfo) {
		this.otherInfo = otherInfo;
	}
	
	public void setProperty(String key, Object value) {
		if (otherInfo == null)
			otherInfo = new HashMap<String, Object>();
		otherInfo.put(key, value);
	}
	
	public Object getProperty(String key) {
		if (otherInfo == null)
			otherInfo = new HashMap<String, Object>();
		return otherInfo.get(key);
	}
}
