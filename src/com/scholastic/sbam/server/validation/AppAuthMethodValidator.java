package com.scholastic.sbam.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.AuthMethod;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.objects.DbAuthMethod;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.objects.DbSite;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;
import com.scholastic.sbam.shared.util.AppConstants;

public class AppAuthMethodValidator {
	
	public static int MIN_CODE_LEN = 2;
	
	private List<String> messages = new ArrayList<String>();
	
	private	AuthMethodInstance original;
	private AuthMethod		 authMethod;

	public List<String> validateAuthMethod(AuthMethodInstance instance) {
		if (instance.getStatus() == AppConstants.STATUS_DELETED)
			return null;

		patchInstance(instance);
		
		validateAuthMethodId(instance.getAgreementId(), instance.getUcn(), instance.getUcnSuffix(), instance.getSiteLocCode(), instance.getMethodType(), instance.getMethodKey(), instance.isNewRecord());
		validateInstitution(instance.getUcn());
		validateSite(instance.getUcn(), instance.getUcnSuffix(), instance.getSiteLocCode());
		validateStatus(instance.getStatus());
		return messages;
	}
	
	public void patchInstance(AuthMethodInstance instance) {
//		if (instance.getStatus() == AppConstants.STATUS_ANY_NONE)
//			instance.setStatus(AppConstants.STATUS_ACTIVE);

		if (instance.getUcnSuffix() == 0)
			instance.setUcnSuffix(1);
		
		if (instance.getSiteLocCode() == null)
			instance.setSiteLocCode("");
		
	}
	
	public List<String> validateAuthMethodId(int agreementId, int ucn, int ucnSuffix, String siteLocCode, String methodType, String methodCode, boolean isNew) {
		if (isNew) {
			validateNewAuthMethodId(agreementId, ucn, ucnSuffix, siteLocCode, methodType, methodCode);
		} else {
			validateOldAuthMethodId(agreementId, ucn, ucnSuffix, siteLocCode, methodType, methodCode);
		}
		return messages;
	}
	
	public List<String> validateOldAuthMethodId(int agreementId, int ucn, int ucnSuffix, String siteLocCode, String methodType, String methodCode) {
		
		if (!loadAuthMethod())
			return messages;
		
		if (agreementId <= 0 && ucn <= 0) {
			addMessage("An agreement ID or UCN is required.");
			return messages;
		}
		
		if (authMethod.getId().getAgreementId() != agreementId)
			addMessage("Agreement ID cannot be changed.");
		
		if (authMethod.getId().getAgreementId() == 0 && authMethod.getId().getUcn() != ucn)
			addMessage("UCN cannot be changed.");
		
		if (authMethod.getId().getAgreementId() == 0 && authMethod.getId().getUcnSuffix() != ucnSuffix)
			addMessage("UCN suffix cannot be changed.");
		
		if (authMethod.getId().getAgreementId() == 0 && !siteLocCode.equals(authMethod.getId().getSiteLocCode()))
			addMessage("Site location code cannot be changed.");
		
		return messages;
	}
	
	public List<String> validateNewAuthMethodId(int agreementId, int ucn, int ucnSuffix, String siteLocCode, String methodType, String methodCode) {
		if (agreementId > 0 && ucn > 0 && siteLocCode != null) {
			AuthMethod conflict = DbAuthMethod.getById(agreementId, ucn, ucnSuffix, siteLocCode, methodType, methodCode);
			if (conflict != null) {
				addMessage("Authentication Method already exists.");
			}
		}
		return messages;
	}
	
	public List<String> validateInstitution(int ucn) {
		if (ucn > 0) {
			Institution institution = DbInstitution.getByCode(ucn);
			if (institution == null) {
				addMessage("Institution not found in the database.");
			}
		}
		return messages;
	}
	
	public List<String> validateSite(int ucn, int ucnSuffix, String siteLocCode) {
		if (ucn > 0 && siteLocCode != null && siteLocCode.length() > 0) {
			Site site = DbSite.getById(ucn, ucnSuffix, siteLocCode);
			if (site == null) {
				addMessage("Site not found in the database.");
			}
		}
		return messages;
	}
	
	public List<String> validateStatus(char status) {
		if (status != AppConstants.STATUS_ACTIVE && status != AppConstants.STATUS_INACTIVE && status != AppConstants.STATUS_DELETED)
			addMessage("Invalid status " + status);
		return messages;
	}
	
	private boolean loadAuthMethod() {
		if (authMethod == null) {
			authMethod = DbAuthMethod.getById(original.getAgreementId(), original.getUcn(), original.getUcnSuffix(), original.getSiteLocCode(), original.getMethodType(), original.getMethodKey());
			if (authMethod == null) {
				addMessage("Unexpected Error: Original agreement site not found in the database.");
				return false;
			}
		}
		return true;
	}
	
	private void addMessage(String message) {
		if (message != null && message.length() > 0)
			messages.add(message);
	}

	public AuthMethodInstance getOriginal() {
		return original;
	}

	public void setOriginal(AuthMethodInstance original) {
		this.original = original;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
