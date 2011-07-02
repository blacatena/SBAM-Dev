package com.scholastic.sbam.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.Snapshot;
import com.scholastic.sbam.server.database.objects.DbSnapshot;
import com.scholastic.sbam.shared.objects.SnapshotInstance;
import com.scholastic.sbam.shared.validation.CodeValidator;
import com.scholastic.sbam.shared.validation.NameValidator;

public class AppSnapshotValidator {
	
	public static int MIN_CODE_LEN = 2;
	
	private List<String> messages = new ArrayList<String>();
	
	private	SnapshotInstance original;
	private Snapshot		 snapshot;

	public List<String> validateSnapshot(SnapshotInstance instance) {
		if (instance.getStatus() == 'X')
			return null;
		validateSnapshotCode(instance.getSnapshotCode(), instance.isNewRecord());
		validateSnapshotName(instance.getSnapshotName());
		validateSnapshotType(instance.getSnapshotType());
		validateProductServiceType(instance.getProductServiceType());
		validateStatus(instance.getStatus());
		return messages;
	}
	
	public List<String> validateSnapshotCode(String value, boolean isNew) {
		if (isNew) {
			validateNewSnapshotCode(value);
		} else {
			validateOldSnapshotCode(value);
		}
		return messages;
	}
	
	public List<String> validateOldSnapshotCode(String value) {
		if (!loadSnapshot())
			return messages;
		
		if (value == null || value.length() == 0) {
			addMessage("A snapshot code is required.");
			return messages;
		}
		
		addMessage((new CodeValidator(MIN_CODE_LEN)).validate(value));
		
		if (!snapshot.getSnapshotCode().equals(value))
			addMessage("Snapshot code cannot be changed.");
		
		return messages;
	}
	
	public List<String> validateNewSnapshotCode(String value) {
		addMessage((new CodeValidator(MIN_CODE_LEN)).validate(value));
		if (value != null && value.length() > 0) {
			Snapshot conflict = DbSnapshot.getByCode(value);
			if (conflict != null) {
				addMessage("Snapshot code already exists.");
			}
		}
		return messages;
	}
	
	public List<String> validateSnapshotName(String snapshotName) {
		addMessage(new NameValidator().validate(snapshotName));
		return messages;
	}
	
	public List<String> validateSnapshotType(String snapshotType) {
		//	No validation
		return messages;
	}
	
	public List<String> validateProductServiceType(char productServiceType) {
		if (productServiceType != SnapshotInstance.PRODUCT_TYPE && productServiceType != SnapshotInstance.SERVICE_TYPE && productServiceType != SnapshotInstance.NO_TERM_TYPE) {
			addMessage("Product serviced type must be 'n', 'p' or 's'.");
		}
		return messages;
	}
	
	public List<String> validateStatus(char status) {
		if (status != 0 && status != 'A' && status != 'I' && status != 'X')
			addMessage("Invalid status " + status);
		return messages;
	}
	
	private boolean loadSnapshot() {
		if (snapshot == null) {
			snapshot = DbSnapshot.getByCode(original.getSnapshotCode());
			if (snapshot == null) {
				addMessage("Unexpected Error: Original snapshot code not found in the database.");
				return false;
			}
		}
		return true;
	}
	
	private void addMessage(String message) {
		if (message != null && message.length() > 0)
			messages.add(message);
	}

	public SnapshotInstance getOriginal() {
		return original;
	}

	public void setOriginal(SnapshotInstance original) {
		this.original = original;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
