package com.scholastic.sbam.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.PreferenceCategory;
import com.scholastic.sbam.server.database.objects.DbPreferenceCategory;
import com.scholastic.sbam.shared.objects.PreferenceCategoryInstance;
import com.scholastic.sbam.shared.validation.CodeValidator;
import com.scholastic.sbam.shared.validation.NameValidator;

public class AppPreferenceCategoryValidator {
	
	public static int MIN_CODE_LEN = 2;
	
	private List<String> messages = new ArrayList<String>();
	
	private	PreferenceCategoryInstance original;
	private PreferenceCategory		 preferenceCategory;

	public List<String> validatePreferenceCategory(PreferenceCategoryInstance instance) {
		if (instance.getStatus() == 'X')
			return null;
		validatePreferenceCategoryCode(instance.getPrefCatCode(), instance.isNewRecord());
		validateDescription(instance.getDescription());
		validateStatus(instance.getStatus());
		return messages;
	}
	
	public List<String> validatePreferenceCategoryCode(String value, boolean isNew) {
		if (isNew) {
			validateNewPreferenceCategoryCode(value);
		} else {
			validateOldPreferenceCategoryCode(value);
		}
		return messages;
	}
	
	public List<String> validateOldPreferenceCategoryCode(String value) {
		if (!loadPreferenceCategory())
			return messages;
		
		if (value == null || value.length() == 0) {
			addMessage("A preference category code is required.");
			return messages;
		}
		
		addMessage((new CodeValidator(MIN_CODE_LEN)).validate(value));
		
		if (!preferenceCategory.getPrefCatCode().equals(value))
			addMessage("Preference Category code cannot be changed.");
		
		return messages;
	}
	
	public List<String> validateNewPreferenceCategoryCode(String value) {
		addMessage((new CodeValidator(MIN_CODE_LEN)).validate(value));
		if (value != null && value.length() > 0) {
			PreferenceCategory conflict = DbPreferenceCategory.getByCode(value);
			if (conflict != null) {
				addMessage("Preference Category code already exists.");
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
	
	private boolean loadPreferenceCategory() {
		if (preferenceCategory == null) {
			preferenceCategory = DbPreferenceCategory.getByCode(original.getPrefCatCode());
			if (preferenceCategory == null) {
				addMessage("Unexpected Error: Original preference category code not found in database.");
				return false;
			}
		}
		return true;
	}
	
	private void addMessage(String message) {
		if (message != null && message.length() > 0)
			messages.add(message);
	}

	public PreferenceCategoryInstance getOriginal() {
		return original;
	}

	public void setOriginal(PreferenceCategoryInstance original) {
		this.original = original;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
