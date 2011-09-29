package com.scholastic.sbam.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.Proxy;
import com.scholastic.sbam.server.database.objects.DbProxy;
import com.scholastic.sbam.shared.objects.ProxyInstance;
import com.scholastic.sbam.shared.util.AppConstants;
import com.scholastic.sbam.shared.validation.NameValidator;

public class AppProxyValidator {
	
	public static int MIN_CODE_LEN = 2;
	
	private List<String> messages = new ArrayList<String>();
	
	private	ProxyInstance original;
	private Proxy		  proxy;

	public List<String> validateProxy(ProxyInstance instance) {
		if (instance.getStatus() == 'X')
			return null;
		validateProxyId(instance.getProxyId(), instance.isNewRecord());
		validateDescription(instance.getDescription());
		validateStatus(instance.getStatus());
		return messages;
	}
	
	public List<String> validateProxyId(int value, boolean isNew) {
		if (isNew) {
			validateNewProxyId(value);	
		} else {
			validateOldProxyId(value);
		}
		return messages;
	}
	
	public List<String> validateOldProxyId(int value) {
		if (!loadProxy())
			return messages;
		
		if (value <= 0) {
			addMessage("A proxy ID is required.");
			return messages;
		}
		
		if (proxy.getProxyId() != value)
			addMessage("Proxy ID cannot be changed.");
		
		return messages;
	}
	
	public List<String> validateNewProxyId(int value) {
		if (value > 0) {
			Proxy conflict = DbProxy.getById(value);
			if (conflict != null && conflict.getStatus() != AppConstants.STATUS_DELETED) {
				addMessage("Proxy ID already exists.");
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
	
	private boolean loadProxy() {
		if (proxy == null) {
			proxy = DbProxy.getById(original.getProxyId());
			if (proxy == null) {
				addMessage("Unexpected Error: Original proxy not found in database.");
				return false;
			}
		}
		return true;
	}
	
	private void addMessage(String message) {
		if (message != null && message.length() > 0)
			messages.add(message);
	}

	public ProxyInstance getOriginal() {
		return original;
	}

	public void setOriginal(ProxyInstance original) {
		this.original = original;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
