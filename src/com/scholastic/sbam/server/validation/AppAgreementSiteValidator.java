package com.scholastic.sbam.server.validation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.AgreementSite;
import com.scholastic.sbam.server.database.codegen.CommissionType;
import com.scholastic.sbam.server.database.codegen.CancelReason;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.objects.DbAgreementSite;
import com.scholastic.sbam.server.database.objects.DbCommissionType;
import com.scholastic.sbam.server.database.objects.DbCancelReason;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.objects.DbSite;
import com.scholastic.sbam.shared.objects.AgreementSiteInstance;
import com.scholastic.sbam.shared.util.AppConstants;

public class AppAgreementSiteValidator {
	
	public static int MIN_CODE_LEN = 2;
	
	private List<String> messages = new ArrayList<String>();
	
	private	AgreementSiteInstance original;
	private AgreementSite		 agreementSite;

	public List<String> validateAgreementSite(AgreementSiteInstance instance) {
		if (instance.getStatus() == AppConstants.STATUS_DELETED)
			return null;

		patchInstance(instance);
		
		validateCancelReasonCode(instance.getCancelReasonCode(), instance); // This may change the status to I, set inactive date
		validateAgreementSiteId(instance.getAgreementId(), instance.getSiteUcn(), instance.getSiteUcnSuffix(), instance.getSiteLocCode(), instance.isNewRecord());
		validateInstitution(instance.getSiteUcn());
		validateSite(instance.getSiteUcn(), instance.getSiteUcnSuffix(), instance.getSiteLocCode());
		validateCommissionCode(instance.getCommissionCode(), instance);
		validateStatus(instance.getStatus());
		return messages;
	}
	
	public void patchInstance(AgreementSiteInstance instance) {
//		if (instance.getStatus() == AppConstants.STATUS_ANY_NONE)
//			instance.setStatus(AppConstants.STATUS_ACTIVE);

		if (instance.getSiteUcnSuffix() == 0)
			instance.setSiteUcnSuffix(1);
		
		if (instance.getSiteLocCode() == null)
			instance.setSiteLocCode("");
		
	}
	
	public List<String> validateAgreementSiteId(int agreementId, int ucn, int ucnSuffix, String siteLocCode, boolean isNew) {
		if (isNew) {
			validateNewAgreementSiteId(agreementId, ucn, ucnSuffix, siteLocCode);
		} else {
			validateOldAgreementSiteId(agreementId, ucn, ucnSuffix, siteLocCode);
		}
		return messages;
	}
	
	public List<String> validateOldAgreementSiteId(int agreementId, int ucn, int ucnSuffix, String siteLocCode) {
		
		if (!loadAgreementSite())
			return messages;
		
		if (agreementId <= 0) {
			addMessage("An agreement ID is required.");
			return messages;
		}
		
		if (ucn <= 0) {
			addMessage("A UCN is required.");
			return messages;
		}
		
		if (agreementSite.getId().getAgreementId() != agreementId)
			addMessage("Agreement ID cannot be changed.");
		
		if (agreementSite.getId().getSiteUcn() != ucn)
			addMessage("UCN cannot be changed.");
		
		if (agreementSite.getId().getSiteUcnSuffix() != ucnSuffix)
			addMessage("UCN suffix cannot be changed.");
		
		if (!siteLocCode.equals(agreementSite.getId().getSiteLocCode()))
			addMessage("Site location code cannot be changed.");
		
		return messages;
	}
	
	public List<String> validateNewAgreementSiteId(int agreementId, int ucn, int ucnSuffix, String siteLocCode) {
		if (agreementId > 0 && ucn > 0 && siteLocCode != null) {
			AgreementSite conflict = DbAgreementSite.getById(agreementId, ucn, ucnSuffix, siteLocCode);
			if (conflict != null && conflict.getStatus() != AppConstants.STATUS_DELETED) {
				addMessage("Agreement Site already exists.");
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
	
	public List<String> validateCommissionCode(String commissionCode, AgreementSiteInstance instance) {
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
	
	public List<String> validateCancelReasonCode(String cancelReasonCode, AgreementSiteInstance instance) {
		if (cancelReasonCode == null)
			return messages;
		
		if (cancelReasonCode.length() == 0) {
			instance.setStatus(AppConstants.STATUS_ACTIVE);
			return messages;
		}

		CancelReason cReason = DbCancelReason.getByCode(cancelReasonCode);
		if (cReason == null)
			addMessage("Cancel reason code '" + cancelReasonCode + "' not found in the database.");
		else {
			instance.setCancelReason(DbCancelReason.getInstance(cReason));
			if (instance.getStatus() != AppConstants.STATUS_DELETED)
				instance.setStatus(AppConstants.STATUS_INACTIVE);
			if (instance.getInactiveDate() == null)
				instance.setInactiveDate(new Date());
		}
		
		return messages;
	}
	
	public List<String> validateStatus(char status) {
		if (status != AppConstants.STATUS_ACTIVE && status != AppConstants.STATUS_INACTIVE && status != AppConstants.STATUS_DELETED)
			addMessage("Invalid status " + status);
		return messages;
	}
	
	private boolean loadAgreementSite() {
		if (agreementSite == null) {
			agreementSite = DbAgreementSite.getById(original.getAgreementId(), original.getSiteUcn(), original.getSiteUcnSuffix(), original.getSiteLocCode());
			if (agreementSite == null) {
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

	public AgreementSiteInstance getOriginal() {
		return original;
	}

	public void setOriginal(AgreementSiteInstance original) {
		this.original = original;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
