package com.scholastic.sbam.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.AgreementContact;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.Contact;
import com.scholastic.sbam.server.database.objects.DbAgreementContact;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.objects.DbContact;
import com.scholastic.sbam.shared.objects.AgreementContactInstance;
import com.scholastic.sbam.shared.objects.ContactInstance;
import com.scholastic.sbam.shared.util.AppConstants;

public class AppAgreementContactValidator {
	
	public static int MIN_CODE_LEN = 2;
	
	private List<String> messages = new ArrayList<String>();
	
	private	AgreementContactInstance original;
	private AgreementContact		 agreementContact;

	public List<String> validateAgreementContact(AgreementContactInstance instance) {
		if (instance.getStatus() == AppConstants.STATUS_DELETED)
			return null;
		
		validateAgreementContactId(instance.getAgreementId(), instance.getContactId(), instance.isNewRecord());
//		validateInstitution(instance.getContactUcn());
		validateContact(instance.getContact());
		validateStatus(instance.getStatus());
		return messages;
	}
	
	public List<String> validateAgreementContactId(int agreementId, int contactId, boolean isNew) {
		if (isNew) {
			validateNewAgreementContactId(agreementId, contactId);
		} else {
			validateOldAgreementContactId(agreementId, contactId);
		}
		return messages;
	}
	
	public List<String> validateOldAgreementContactId(int agreementId, int contactId) {
		
		if (!loadAgreementContact())
			return messages;
		
		if (agreementId <= 0) {
			addMessage("An agreement ID is required.");
			return messages;
		}
		
		if (contactId <= 0) {
			addMessage("A Contact ID is required.");
			return messages;
		}
		
		if (agreementContact.getId().getAgreementId() != agreementId)
			addMessage("Agreement ID cannot be changed.");
		
		if (agreementContact.getId().getContactId() != contactId)
			addMessage("Contact ID cannot be changed.");
		
		return messages;
	}
	
	public List<String> validateNewAgreementContactId(int agreementId, int contactId) {
		if (agreementId > 0 && contactId > 0) {
			AgreementContact conflict = DbAgreementContact.getById(agreementId, contactId);
			if (conflict != null) {
				addMessage("Agreement Contact already exists.");
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
	
	public List<String> validateContact(ContactInstance contact) {
		if (contact == null) {
			addMessage("The contact data is missing.");
		} else {
			if (contact.getContactId() > 0 && !contact.isNewRecord()) {
				Contact dbContact = DbContact.getByCode(contact.getContactId());
				if (dbContact == null) {
					addMessage("Contact not found in the database.");
				}
			}
			if (contact.getFullName() == null || contact.getFullName().trim().length() == 0) {
				addMessage("A name is required.");
			}
		}
		
		
		return messages;
	}
	
	public List<String> validateContact(int contactId) {
		if (contactId > 0) {
			Contact contact = DbContact.getByCode(contactId);
			if (contact == null) {
				addMessage("Contact not found in the database.");
			}
		}
		return messages;
	}
	
	public List<String> validateStatus(char status) {
		if (status != AppConstants.STATUS_ANY_NONE && status != AppConstants.STATUS_ACTIVE && status != AppConstants.STATUS_INACTIVE && status != AppConstants.STATUS_DELETED)
			addMessage("Invalid status " + status);
		return messages;
	}
	
	private boolean loadAgreementContact() {
		if (agreementContact == null) {
			agreementContact = DbAgreementContact.getById(original.getAgreementId(), original.getContactId());
			if (agreementContact == null) {
				addMessage("Unexpected Error: Original agreement contact not found in the database.");
				return false;
			}
		}
		return true;
	}
	
	private void addMessage(String message) {
		if (message != null && message.length() > 0)
			messages.add(message);
	}

	public AgreementContactInstance getOriginal() {
		return original;
	}

	public void setOriginal(AgreementContactInstance original) {
		this.original = original;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
