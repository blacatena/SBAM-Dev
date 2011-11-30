package com.scholastic.sbam.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.StatsAdmin;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.objects.DbStatsAdmin;
import com.scholastic.sbam.shared.objects.StatsAdminInstance;
import com.scholastic.sbam.shared.util.AppConstants;

public class AppStatsAdminValidator {
	
	public static int MIN_CODE_LEN = 2;
	
	private List<String> messages = new ArrayList<String>();
	
	private	StatsAdminInstance original;
	private StatsAdmin		 StatsAdmin;

	public List<String> validateStatsAdmin(StatsAdminInstance instance) {
		if (instance.getStatus() == AppConstants.STATUS_DELETED)
			return null;

		patchInstance(instance);
		
		validateStatsAdminId(instance.getUcn(), instance.isNewRecord());
		validateInstitution(instance.getUcn());
		validateAdminUid(instance.getAdminUid(), instance.getAdminPassword());
		validateStatus(instance.getStatus());
		return messages;
	}
	
	public void patchInstance(StatsAdminInstance instance) {
//		if (instance.getStatus() == AppConstants.STATUS_ANY_NONE)
//			instance.setStatus(AppConstants.STATUS_ACTIVE);
	}
	
	public List<String> validateStatsAdminId(int ucn, boolean isNew) {
		if (isNew) {
			validateNewStatsAdminId(ucn);
		} else {
			validateOldStatsAdminId(ucn);
		}
		return messages;
	}
	
	public List<String> validateOldStatsAdminId(int ucn) {
		
		if (!loadStatsAdmin())
			return messages;
		
		if (ucn <= 0) {
			addMessage("A UCN is required.");
			return messages;
		}
		
		if (StatsAdmin.getUcn() != ucn)
			addMessage("UCN cannot be changed.");
		
		
		return messages;
	}
	
	public List<String> validateNewStatsAdminId(int ucn) {
		if (ucn <= 0) {
			addMessage("A UCN is required.");
		}
		if (ucn > 0) {
			StatsAdmin conflict = DbStatsAdmin.getById(ucn);
			if (conflict != null && conflict.getStatus() != AppConstants.STATUS_DELETED) {
				addMessage("Stats Admin entry already exists.");
			}
		}
		return messages;
	}
	
	public List<String> validateInstitution(int ucn) {
		if (ucn > 0) {
			Institution institution = DbInstitution.getByCode(ucn);
			if (institution == null) {
				addMessage("Institution " + ucn + " not found in the database.");
			}
		}
		return messages;
	}
	
	public List<String> validateStatsAdmin(int ucn, int ucnSuffix, String StatsAdminLocCode) {
		if (ucn > 0 && StatsAdminLocCode != null && StatsAdminLocCode.length() > 0) {
			StatsAdmin StatsAdmin = DbStatsAdmin.getById(ucn);
			if (StatsAdmin == null) {
				addMessage("Stats Admin entry not found in the database.");
			}
		}
		return messages;
	}
	
	public List<String> validateAdminUid(String adminUid, String adminPassword) {
		if (adminUid == null || adminUid.length() == 0 ) {
			if (adminPassword != null && adminPassword.length() > 0)
				addMessage("An Admin UID is required for a password.");
		} else {
			if (adminPassword == null || adminPassword.length() == 0)
				addMessage("A password is required with an Admin UID.");
		}
		return messages;
	}
	
	public List<String> validateStatus(char status) {
		if (status != AppConstants.STATUS_ACTIVE && status != AppConstants.STATUS_INACTIVE && status != AppConstants.STATUS_DELETED)
			addMessage("Invalid status " + status);
		return messages;
	}
	
	private boolean loadStatsAdmin() {
		if (StatsAdmin == null) {
			StatsAdmin = DbStatsAdmin.getById(original.getUcn());
			if (StatsAdmin == null) {
				addMessage("Unexpected Error: Original Admin Stats not found in the database.");
				return false;
			}
		}
		return true;
	}
	
	private void addMessage(String message) {
		if (message != null && message.length() > 0)
			messages.add(message);
	}

	public StatsAdminInstance getOriginal() {
		return original;
	}

	public void setOriginal(StatsAdminInstance original) {
		this.original = original;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
