package com.scholastic.sbam.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.User;
import com.scholastic.sbam.server.database.objects.DbUser;
import com.scholastic.sbam.shared.objects.UserInstance;
import com.scholastic.sbam.shared.util.AppConstants;
import com.scholastic.sbam.shared.validation.AppRoleGroupValidator;
import com.scholastic.sbam.shared.validation.AppUserNameValidator;
import com.scholastic.sbam.shared.validation.EmailValidator;
import com.scholastic.sbam.shared.validation.NameValidator;

public class AppUserValidator {
	
	private List<String> messages = new ArrayList<String>();
	
	private	UserInstance original;
	private User		 user;

	public List<String> validateUser(UserInstance instance) {
		if (instance.getStatus() == 'X')
			return null;
		validateUserName(instance.getUserName(), instance.isNewRecord());
		validateEmail(instance.getEmail());
		validateRoleGroupTitle(instance.getRoleGroupTitle());
		validateName(instance.getFirstName());
		validateName(instance.getLastName());
		return messages;
	}
	
	public List<String> validateUserName(String value, boolean isNew) {
		if (isNew) {
			validateNewUserName(value);
		} else {
			validateOldUserName(value);
		} 
		return messages;
	}
	
	public List<String> validateOldUserName(String value) {
		if (!loadUser())
			return messages;
		
		if (value == null || value.length() == 0) {
			addMessage("A user name is required.");
			return messages;
		}
		
		addMessage((new AppUserNameValidator()).validate(value));
		
		if (!user.getUserName().equals(value))
			addMessage("User name cannot be changed.");
		
		return messages;
	}
	
	public List<String> validateNewUserName(String value) {
		addMessage((new AppUserNameValidator()).validate(value));
		if (value != null && value.length() > 0) {
			User conflict = DbUser.getByUserName(value);
			if (conflict != null && conflict.getStatus() != AppConstants.STATUS_DELETED) {
				addMessage("User name already exists.");
			}
		}
		return messages;
	}
	
	public List<String> validateName(String name) {
		addMessage(new NameValidator().validate(name));
		return messages;
	}
	
	public List<String> validateEmail(String email) {
		addMessage(new EmailValidator().validate(email));
		return messages;
	}
	
	public List<String> validateRoleGroupTitle(String roleGroupTitle) {
		addMessage(new AppRoleGroupValidator().validate(roleGroupTitle));
		return messages;
	}
	
	private boolean loadUser() {
		if (user == null) {
			user = DbUser.getById(original.getId());
			if (user == null) {
				addMessage("Unexpected Error: Original user not found in database.");
				return false;
			}
		}
		return true;
	}
	
	private void addMessage(String message) {
		if (message != null && message.length() > 0)
			messages.add(message);
	}

	public UserInstance getOriginal() {
		return original;
	}

	public void setOriginal(UserInstance original) {
		this.original = original;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
