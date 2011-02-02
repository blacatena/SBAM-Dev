package com.scholastic.sbam.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.PreferenceCategory;
import com.scholastic.sbam.server.database.codegen.PreferenceCode;
import com.scholastic.sbam.server.database.objects.DbPreferenceCategory;
import com.scholastic.sbam.server.database.objects.DbPreferenceCode;
import com.scholastic.sbam.shared.objects.PreferenceCodeInstance;
import com.scholastic.sbam.shared.validation.CodeValidator;
import com.scholastic.sbam.shared.validation.NameValidator;

public class AppPreferenceCodeValidator {
	
	public static int MIN_CODE_LEN = 2;
	
	private List<String> messages = new ArrayList<String>();
	
	private	PreferenceCodeInstance	original;
	private PreferenceCategory		preferenceCategory;
	private PreferenceCode			preferenceCode;

	public List<String> validatePreferenceCode(PreferenceCodeInstance instance) {
		if (instance.getStatus() == 'X')
			return null;
		validatePreferenceCategoryCode(instance.getPrefCatCode());
		validatePreferenceSelectionCode(instance.getPrefSelCode(), instance.isNewRecord());
		validateDescription(instance.getDescription());
		validateStatus(instance.getStatus());
		return messages;
	}
	
	public List<String> validatePreferenceCategoryCode(String value) {
		if (!loadPreferenceCategory())
			return messages;
		
		if (value == null || value.length() == 0) {
			addMessage("A preference category code is required.");
			return messages;
		}
		
		addMessage((new CodeValidator(MIN_CODE_LEN)).validate(value));
		
		if (!preferenceCategory.getPrefCatCode().equals(value))
			addMessage("Preference Category code cannot be changed.");
		
		if (preferenceCategory.getStatus() == 'X')
			addMessage("This preference category is marked as deleted.");
		if (preferenceCategory.getStatus() == 'I')
			addMessage("This preference category is marked as inactive.");

		return messages;
	}
	
	public List<String> validatePreferenceSelectionCode(String value, boolean isNew) {
		if (isNew) {
			validateNewPreferenceSelectionCode(value);
		} else {
			validateOldPreferenceSelectionCode(value);
		}
		return messages;
	}
	
	public List<String> validateOldPreferenceSelectionCode(String value) {
		if (!loadPreferenceSelection())
			return messages;
		
		if (value == null || value.length() == 0) {
			addMessage("A preference selection code is required.");
			return messages;
		}
		
		addMessage((new CodeValidator(MIN_CODE_LEN)).validate(value));
		
		if (!preferenceCategory.getPrefCatCode().equals(value))
			addMessage("Preference selection code cannot be changed.");
		
		return messages;
	}
	
	public List<String> validateNewPreferenceSelectionCode(String value) {
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
	
	private boolean loadPreferenceSelection() {
		if (preferenceCode == null) {
			preferenceCode = DbPreferenceCode.getByCode(original.getPrefCatCode(), original.getPrefSelCode());
			if (preferenceCode == null) {
				addMessage("Unexpected Error: Original preference selection code not found in database.");
				return false;
			}
		}
		return true;
	}
	
	private void addMessage(String message) {
		if (message != null && message.length() > 0)
			messages.add(message);
	}

	public PreferenceCodeInstance getOriginal() {
		return original;
	}

	public void setOriginal(PreferenceCodeInstance original) {
		this.original = original;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
