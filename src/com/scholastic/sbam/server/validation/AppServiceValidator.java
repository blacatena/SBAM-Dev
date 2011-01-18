package com.scholastic.sbam.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.Service;
import com.scholastic.sbam.server.database.objects.DbService;
import com.scholastic.sbam.shared.objects.ServiceInstance;
import com.scholastic.sbam.shared.validation.CodeValidator;
import com.scholastic.sbam.shared.validation.NameValidator;

public class AppServiceValidator {
	
	private List<String> messages = new ArrayList<String>();
	
	private	ServiceInstance original;
	private Service		 cancelReason;

	public List<String> validateService(ServiceInstance instance) {
		if (instance.getStatus() == 'X')
			return null;
		validateServiceCode(instance.getServiceCode());
		validateDescription(instance.getDescription());
		validateServiceType(instance.getServiceType());
		validateExportValue(instance.getExportValue());
		validateExportFile(instance.getExportFile());
		validateStatus(instance.getStatus());
		return messages;
	}
	
	public List<String> validateServiceCode(String value) {
		if (original.getServiceCode() != null && original.getServiceCode().length() > 0) {
			validateOldServiceCode(value);
		} else {
			validateNewServiceCode(value);
		}
		return messages;
	}
	
	public List<String> validateOldServiceCode(String value) {
		if (!loadService())
			return messages;
		
		if (value == null || value.length() == 0) {
			addMessage("A service code is required.");
			return messages;
		}
		
		addMessage((new CodeValidator(2)).validate(value));
		
		if (!cancelReason.getServiceCode().equals(value))
			addMessage("Service code cannot be changed.");
		
		return messages;
	}
	
	public List<String> validateNewServiceCode(String value) {
		addMessage((new CodeValidator()).validate(value));
		if (value != null && value.length() > 0) {
			Service conflict = DbService.getByCode(value);
			if (conflict != null) {
				addMessage("Service code already exists.");
			}
		}
		return messages;
	}
	
	public List<String> validateDescription(String description) {
		addMessage(new NameValidator().validate(description));
		return messages;
	}
	
	public List<String> validateExportValue(String exportValue) {
		addMessage(new NameValidator("export value").validate(exportValue));
		return messages;
	}
	
	public List<String> validateExportFile(String exportFile) {
		addMessage(new NameValidator("export file").validate(exportFile));
		return messages;
	}
	
	public List<String> validateStatus(char status) {
		if (status != 0 && status != 'A' && status != 'I' && status != 'X')
			addMessage("Invalid status " + status);
		return messages;
	}
	
	public List<String> validateServiceType(char serviceType) {
		if (serviceType != 0 && serviceType != 'I' && serviceType != 'A')
			addMessage("Invalid service type " + serviceType);
		return messages;
	}
	
	private boolean loadService() {
		if (cancelReason == null) {
			cancelReason = DbService.getByCode(original.getServiceCode());
			if (cancelReason == null) {
				addMessage("Unexpected Error: Original service code not found in database.");
				return false;
			}
		}
		return true;
	}
	
	private void addMessage(String message) {
		if (message != null && message.length() > 0)
			messages.add(message);
	}

	public ServiceInstance getOriginal() {
		return original;
	}

	public void setOriginal(ServiceInstance original) {
		this.original = original;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
