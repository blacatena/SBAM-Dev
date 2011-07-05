package com.scholastic.sbam.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.Snapshot;
import com.scholastic.sbam.server.database.objects.DbSnapshot;
import com.scholastic.sbam.shared.objects.SnapshotInstance;
import com.scholastic.sbam.shared.validation.NameValidator;

public class AppSnapshotValidator {
	
	public static int MIN_CODE_LEN = 2;
	
	private List<String> messages = new ArrayList<String>();
	
	private	SnapshotInstance original;
	private Snapshot		 snapshot;

	public List<String> validateSnapshot(SnapshotInstance instance) {
		if (instance.getStatus() == 'X')
			return null;
		validateSnapshotId(instance.getSnapshotId(), instance.isNewRecord());
		validateSnapshotName(instance.getSnapshotName());
		validateSnapshotType(instance.getSnapshotType());
		validateProductServiceType(instance.getProductServiceType());
		validateStatus(instance.getStatus());
		return messages;
	}
	
	public List<String> validateSnapshotId(int value, boolean isNew) {
		if (isNew) {
			validateNewSnapshotId(value);
		} else {
			validateOldSnapshotId(value);
		}
		return messages;
	}
	
	public List<String> validateOldSnapshotId(int value) {
		if (!loadSnapshot())
			return messages;
		
		if (value <= 0) {
			addMessage("A snapshot id is required.");
			return messages;
		}
		
		if (!snapshot.getSnapshotId().equals(value))
			addMessage("Snapshot id cannot be changed.");
		
		return messages;
	}
	
	public List<String> validateNewSnapshotId(int value) {
		if (value > 0) {
			Snapshot conflict = DbSnapshot.getById(value);
			if (conflict != null) {
				addMessage("Snapshot id already exists.");
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
			snapshot = DbSnapshot.getById(original.getSnapshotId());
			if (snapshot == null) {
				addMessage("Unexpected Error: Original snapshot id not found in the database.");
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
