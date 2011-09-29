package com.scholastic.sbam.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.CommissionType;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.objects.DbCommissionType;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.objects.DbSite;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.util.AppConstants;

public class AppSiteValidator {
	
	public static int MIN_CODE_LEN = 2;
	
	private List<String> messages = new ArrayList<String>();
	
	private	SiteInstance original;
	private Site		 site;

	public List<String> validateSite(SiteInstance instance) {
		if (instance.getStatus() == AppConstants.STATUS_DELETED)
			return null;

		patchInstance(instance);
		
		validateSiteId(instance.getUcn(), instance.getUcnSuffix(), instance.getSiteLocCode(), instance.isNewRecord());
		validateInstitution(instance.getUcn());
		validateCommissionCode(instance.getCommissionCode(), instance);
		validateStatus(instance.getStatus());
		return messages;
	}
	
	public void patchInstance(SiteInstance instance) {
//		if (instance.getStatus() == AppConstants.STATUS_ANY_NONE)
//			instance.setStatus(AppConstants.STATUS_ACTIVE);

		if (instance.getUcnSuffix() == 0)
			instance.setUcnSuffix(1);
		
		if (instance.getSiteLocCode() == null)
			instance.setSiteLocCode("");
		
	}
	
	public List<String> validateSiteId(int ucn, int ucnSuffix, String siteLocCode, boolean isNew) {
		if (isNew) {
			validateNewSiteId(ucn, ucnSuffix, siteLocCode);
		} else {
			validateOldSiteId(ucn, ucnSuffix, siteLocCode);
		}
		return messages;
	}
	
	public List<String> validateOldSiteId(int ucn, int ucnSuffix, String siteLocCode) {
		
		if (!loadSite())
			return messages;
		
		if (ucn <= 0) {
			addMessage("A UCN is required.");
			return messages;
		}
		
		if (site.getId().getUcn() != ucn)
			addMessage("UCN cannot be changed.");
		
		if (site.getId().getUcnSuffix() != ucnSuffix)
			addMessage("UCN suffix cannot be changed.");
		
		if (!siteLocCode.equals(site.getId().getSiteLocCode()))
			addMessage("Site location code cannot be changed.");
		
		return messages;
	}
	
	public List<String> validateNewSiteId(int ucn, int ucnSuffix, String siteLocCode) {
		if (ucn > 0 && siteLocCode != null) {
			Site conflict = DbSite.getById(ucn, ucnSuffix, siteLocCode);
			if (conflict != null && conflict.getStatus() != AppConstants.STATUS_DELETED) {
				addMessage("Site location already exists.");
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
				addMessage("Site location not found in the database.");
			}
		}
		return messages;
	}
	
	public List<String> validateCommissionCode(String commissionCode, SiteInstance instance) {
		if (commissionCode == null || commissionCode.length() == 0)
			return messages;
		else {
			CommissionType cType = DbCommissionType.getByCode(commissionCode);
			if (cType == null)
				addMessage("Commission type code '" + commissionCode + "' not found in the database.");
			else
				instance.setCommissionType(DbCommissionType.getInstance(cType));
		}
		return messages;
	}
	
	public List<String> validateStatus(char status) {
		if (status != AppConstants.STATUS_ACTIVE && status != AppConstants.STATUS_INACTIVE && status != AppConstants.STATUS_DELETED)
			addMessage("Invalid status " + status);
		return messages;
	}
	
	private boolean loadSite() {
		if (site == null) {
			site = DbSite.getById(original.getUcn(), original.getUcnSuffix(), original.getSiteLocCode());
			if (site == null) {
				addMessage("Unexpected Error: Original site location not found in the database.");
				return false;
			}
		}
		return true;
	}
	
	private void addMessage(String message) {
		if (message != null && message.length() > 0)
			messages.add(message);
	}

	public SiteInstance getOriginal() {
		return original;
	}

	public void setOriginal(SiteInstance original) {
		this.original = original;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
