package com.scholastic.sbam.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.AgreementLink;
import com.scholastic.sbam.server.database.codegen.LinkType;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.objects.DbAgreementLink;
import com.scholastic.sbam.server.database.objects.DbLinkType;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.shared.objects.AgreementLinkInstance;
import com.scholastic.sbam.shared.util.AppConstants;
import com.scholastic.sbam.shared.validation.NameValidator;

public class AppAgreementLinkValidator {
	
	public static int MIN_CODE_LEN = 2;
	
	private List<String> messages = new ArrayList<String>();
	
	private	AgreementLinkInstance original;
	private AgreementLink		  agreementLink;

	public List<String> validateAgreementLink(AgreementLinkInstance instance) {
		if (instance.getStatus() == 'X')
			return null;
		validateAgreementLinkId(instance.getLinkId(), instance.isNewRecord());
		validateLinkType(instance.getLinkTypeCode(), instance);
		validateUcn(instance.getUcn(), instance);
		validateStatus(instance.getStatus());
		return messages;
	}
	
	public List<String> validateAgreementLinkId(int value, boolean isNew) {
		if (isNew) {
			validateNewAgreementLinkId(value);	
		} else {
			validateOldAgreementLinkId(value);
		}
		return messages;
	}
	
	public List<String> validateOldAgreementLinkId(int value) {
		if (!loadAgreementLink())
			return messages;
		
		if (value <= 0) {
			addMessage("A agreement link ID is required.");
			return messages;
		}
		
		if (agreementLink.getLinkId() != value)
			addMessage("Agreement link ID cannot be changed.");
		
		return messages;
	}
	
	public List<String> validateNewAgreementLinkId(int value) {
		if (value > 0) {
			AgreementLink conflict = DbAgreementLink.getById(value);
			if (conflict != null && conflict.getStatus() != AppConstants.STATUS_DELETED) {
				addMessage("Agreement link ID already exists.");
			}
		}
		return messages;
	}
	
	public List<String> validateUcn(int ucn, AgreementLinkInstance instance) {
		if (ucn <= 0)
			addMessage("UCN is required.");
		else {
			Institution inst = DbInstitution.getByCode(ucn);
			if (inst == null)
				addMessage("UCN '" + ucn + "' not found in the database.");
			else
				instance.setInstitution(DbInstitution.getInstance(inst));
		}
		return messages;
	}
	
	public List<String> validateLinkType(String linkType, AgreementLinkInstance instance) {
		if (linkType == null || linkType.length() == 0)
			addMessage("A link type is required.");
		else {
			LinkType aType = DbLinkType.getByCode(linkType);
			if (aType == null)
				addMessage("Link type '" + linkType + "' not found in the database.");
			else
				instance.setLinkType(DbLinkType.getInstance(aType));
		}
		return messages;
	}
	
	public List<String> validateDescription(String description) {
		addMessage(new NameValidator().validate(description));
		return messages;
	}
	
	public List<String> validateStatus(char status) {
		if (status != 0 && status != 'A' && status != 'I' && status != 'X')
			addMessage("Invalid status " + status);
		return messages;
	}
	
	private boolean loadAgreementLink() {
		if (agreementLink == null) {
			agreementLink = DbAgreementLink.getById(original.getLinkId());
			if (agreementLink == null) {
				addMessage("Unexpected Error: Original agreement link not found in database.");
				return false;
			}
		}
		return true;
	}
	
	private void addMessage(String message) {
		if (message != null && message.length() > 0)
			messages.add(message);
	}

	public AgreementLinkInstance getOriginal() {
		return original;
	}

	public void setOriginal(AgreementLinkInstance original) {
		this.original = original;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
