package com.scholastic.sbam.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.InstitutionContact;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.Contact;
import com.scholastic.sbam.server.database.objects.DbInstitutionContact;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.objects.DbContact;
import com.scholastic.sbam.shared.objects.InstitutionContactInstance;
import com.scholastic.sbam.shared.objects.ContactInstance;
import com.scholastic.sbam.shared.util.AppConstants;

public class AppInstitutionContactValidator {
	
	public static int MIN_CODE_LEN = 2;
	
	private List<String> messages = new ArrayList<String>();
	
	private	InstitutionContactInstance original;
	private InstitutionContact		 institutionContact;

	public List<String> validateInstitutionContact(InstitutionContactInstance instance) {
		if (instance.getStatus() == AppConstants.STATUS_DELETED)
			return null;
		
		validateInstitutionContactId(instance.getUcn(), instance.getContactId(), instance.isNewRecord());
//		validateInstitution(instance.getContactUcn());
		validateContact(instance.getContact());
		validateStatus(instance.getStatus());
		return messages;
	}
	
	public List<String> validateInstitutionContactId(int ucn, int contactId, boolean isNew) {
		if (isNew) {
			validateNewInstitutionContactId(ucn, contactId);
		} else {
			validateOldInstitutionContactId(ucn, contactId);
		}
		return messages;
	}
	
	public List<String> validateOldInstitutionContactId(int ucn, int contactId) {
		
		if (!loadInstitutionContact())
			return messages;
		
		if (ucn <= 0) {
			addMessage("A UCN is required.");
			return messages;
		}
		
		if (contactId <= 0) {
			addMessage("A Contact ID is required.");
			return messages;
		}
		
		if (institutionContact.getId().getUcn() != ucn)
			addMessage("UCN cannot be changed.");
		
		if (institutionContact.getId().getContactId() != contactId)
			addMessage("Contact ID cannot be changed.");
		
		return messages;
	}
	
	public List<String> validateNewInstitutionContactId(int ucn, int contactId) {
		if (ucn > 0 && contactId > 0) {
			InstitutionContact conflict = DbInstitutionContact.getById(ucn, contactId);
			if (conflict != null && conflict.getStatus() != AppConstants.STATUS_DELETED) {
				addMessage("Institution Contact already exists.");
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
	
	private boolean loadInstitutionContact() {
		if (institutionContact == null) {
			institutionContact = DbInstitutionContact.getById(original.getUcn(), original.getContactId());
			if (institutionContact == null) {
				addMessage("Unexpected Error: Original institution contact not found in the database.");
				return false;
			}
		}
		return true;
	}
	
	private void addMessage(String message) {
		if (message != null && message.length() > 0)
			messages.add(message);
	}

	public InstitutionContactInstance getOriginal() {
		return original;
	}

	public void setOriginal(InstitutionContactInstance original) {
		this.original = original;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
