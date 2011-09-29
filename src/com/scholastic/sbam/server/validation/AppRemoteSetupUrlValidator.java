package com.scholastic.sbam.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.AgreementSite;
import com.scholastic.sbam.server.database.codegen.RemoteSetupUrl;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.objects.DbAgreementSite;
import com.scholastic.sbam.server.database.objects.DbRemoteSetupUrl;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.objects.DbSite;
import com.scholastic.sbam.shared.objects.RemoteSetupUrlInstance;
import com.scholastic.sbam.shared.util.AppConstants;

public class AppRemoteSetupUrlValidator {
	
	public static int MIN_CODE_LEN = 2;
	
	private List<String> messages = new ArrayList<String>();
	
	private	RemoteSetupUrlInstance original;
	private RemoteSetupUrl		 remoteSetupUrl;

	public List<String> validateRemoteSetupUrl(RemoteSetupUrlInstance instance) {
		if (instance.getStatus() == AppConstants.STATUS_DELETED)
			return null;

		patchInstance(instance);
		
		validateRemoteSetupUrlId(instance, instance.isNewRecord());
		if (instance.getUcn() > 0)
			validateInstitution(instance.getUcn());
		validateInstitution(instance.getForUcn());
		validateSite(instance.getForUcn(), instance.getForUcnSuffix(), instance.getForSiteLocCode());
		validateAgreementSite(instance.getAgreementId(), instance.getForUcn(), instance.getForUcnSuffix(), instance.getForSiteLocCode());
		validateStatus(instance.getStatus());
		return messages;
	}
	
	public void patchInstance(RemoteSetupUrlInstance instance) {
		if (instance.isNewRecord() && instance.getStatus() == AppConstants.STATUS_ANY_NONE)
			instance.setStatus(AppConstants.STATUS_ACTIVE);

		//	Patch the key UCN so that its either 0/0/"", or else >0 and >0 and a value
		if (instance.getUcn() == 0) {
			instance.setUcnSuffix(0);
			instance.setSiteLocCode("");
		}
		
		if (instance.getSiteLocCode() == null)
			instance.setSiteLocCode("");

		//	Patch the "for" UCN so that its either 0/0/"", or else >0 and >0 and a value
		if (instance.getForUcn() > 0 && instance.getUcnSuffix() == 0)
			instance.setForUcnSuffix(1);
		if (instance.getForUcn() == 0) {
			instance.setForUcnSuffix(0);
			instance.setForSiteLocCode("");
		}
		
		if (instance.getForSiteLocCode() == null)
			instance.setForSiteLocCode("");
		
		if (instance.getSiteLocCode() == null)
			instance.setSiteLocCode("");
	}
	
	public List<String> validateRemoteSetupUrlId(RemoteSetupUrlInstance instance, boolean isNew) {
		if (isNew) {
			validateNewRemoteSetupUrlId(instance.getAgreementId(), instance.getUcn(), instance.getUcnSuffix(), instance.getSiteLocCode(), instance.getUrlId());
		} else {
			validateOldRemoteSetupUrlId(instance.getAgreementId(), instance.getUcn(), instance.getUcnSuffix(), instance.getSiteLocCode(), instance.getUrlId());
		}
		return messages;
	}
	
	public List<String> validateOldRemoteSetupUrlId(int agreementId, int ucn, int ucnSuffix, String siteLocCode, int urlId) {
		
		if (!loadRemoteSetupUrl())
			return messages;
		
		if (agreementId <= 0 && ucn <= 0) {
			addMessage("An agreement ID or UCN is required.");
			return messages;
		}
		
		if (remoteSetupUrl.getId().getAgreementId() != agreementId)
			addMessage("Agreement ID cannot be changed.");
		
		return messages;
	}
	
	public List<String> validateNewRemoteSetupUrlId(int agreementId, int ucn, int ucnSuffix, String siteLocCode, int urlId) {
		if (agreementId > 0 && ucn > 0 && siteLocCode != null) {
			RemoteSetupUrl conflict = DbRemoteSetupUrl.getById(agreementId, ucn, ucnSuffix, siteLocCode, urlId);
			if (conflict != null && conflict.getStatus() != AppConstants.STATUS_DELETED) {
				addMessage("Remote Setup URL already exists.");
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
	
	/**
	 * For a method associated with both an agreement and a particular site location, be certain the site location is listed on the agreement.
	 * 
	 * @param agreementId
	 * @param ucn
	 * @param ucnSuffix
	 * @param siteLocCode
	 * @return
	 */
	public List<String> validateAgreementSite(int agreementId, int ucn, int ucnSuffix, String siteLocCode) {
		if (agreementId > 0 && ucn > 0 && siteLocCode != null && siteLocCode.length() > 0) {
			//	First look for an "all sites" entry
			AgreementSite site = DbAgreementSite.getById(agreementId, ucn, ucnSuffix, "");
			if (site == null) {
				//	If no "all sites" entry, look for this particular site
				site = DbAgreementSite.getById(agreementId, ucn, ucnSuffix, siteLocCode);
				if (site == null) {
					addMessage("This site location is not (currently) associated with this agreement.");
				}
			}
		}
		return messages;
	}
	
	public List<String> validateStatus(char status) {
		if (status != AppConstants.STATUS_ACTIVE && status != AppConstants.STATUS_INACTIVE && status != AppConstants.STATUS_DELETED)
			addMessage("Invalid status " + status);
		return messages;
	}
	
	private boolean loadRemoteSetupUrl() {
		if (remoteSetupUrl == null) {
			remoteSetupUrl = DbRemoteSetupUrl.getById(original.getAgreementId(), original.getUcn(), original.getUcnSuffix(), original.getSiteLocCode(), original.getUrlId());
			if (remoteSetupUrl == null) {
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

	public RemoteSetupUrlInstance getOriginal() {
		return original;
	}

	public void setOriginal(RemoteSetupUrlInstance original) {
		this.original = original;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
