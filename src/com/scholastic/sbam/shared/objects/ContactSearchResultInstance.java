package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * An instance of a result from a contact search, which may return a contact, institution, or both.
 * @author Bob Lacatena
 *
 */
public class ContactSearchResultInstance implements BeanModelTag, IsSerializable {

	private static BeanModelFactory beanModelfactory;
	
	public static final char CONTACT = 'c';
	public static final char INSTITUTION = 'i';
	public static final char NEW_SEARCH = 'n';
	public static final char ADD_NEW = 'a';

	private char type;
	private ContactInstance contact;
	private InstitutionInstance institution;

	public ContactSearchResultInstance() {
		
	}
	
	public ContactSearchResultInstance(ContactInstance contact) {
		setContact(contact);
	}
	
	public ContactSearchResultInstance(InstitutionInstance institution) {
		setInstitution(institution);
	}
	
	public ContactInstance getContact() {
		return contact;
	}

	public void setContact(ContactInstance contact) {
		this.contact = contact;
		setType();
	}

	public InstitutionInstance getInstitution() {
		return institution;
	}

	public void setInstitution(InstitutionInstance institution) {
		this.institution = institution;
		setType();
	}
	
	public char getType() {
		return type;
	}
	
	public void setType() {
		if (contact != null)
			type = CONTACT;
		else if (institution != null)
			type = INSTITUTION;
		else
			type = ADD_NEW;
	}

	public int getId() {
		if (contact != null)
			return contact.getContactId();
		if (institution != null)
			return institution.getUcn();
		return 0;
	}

	public String getName() {
		if (contact != null)
			return contact.getFullName();
		if (institution != null)
			return institution.getInstitutionName();
		if (type == ADD_NEW)
			return "Create a new contact.";
		if (type == NEW_SEARCH)
			return "Start a new, clean search.";
		return "";
	}
	
	public String getNameInfoHtml() {
		String info = getName();
		if (contact != null) {
			if (contact.getTitle() != null && contact.getTitle().length() > 0)
				info += "<br/>" + contact.getTitle();
			else if (contact.getContactType() != null)
				info += "<br/>" + contact.getContactType().getDescription();
		}
		return info;
	}

	public String getMultiLineAddress() {
		if (contact != null)
			return contact.getHtmlAddress();
		if (institution != null)
			return institution.getHtmlAddress();
		return "";
	}

	public String getPhone() {
		if (contact != null)
			return contact.getPhone();
		if (institution != null)
			return institution.getPhone();
		return "";
	}

	public char getStatus() {
		if (contact != null)
			return contact.getStatus();
		if (institution != null)
			return institution.getStatus();
		return 'A';
	}
	
	public String getListStyle() {
		if (type == ADD_NEW)
			return "list-new";
		if (type == NEW_SEARCH)
			return "list-restart";
		if (contact != null)
			return "list-contact";
		if (institution != null)
			return "list-institution";
		return "list-normal";
	}
	
	/**
	 * We include this property, to line up with the full_name field in the Contact table.  It's really just a synonym for "name".
	 * @return
	 */
	public String getFullName() {
		return getName();
	}
	
	public String getUniqueKey() {
		return type + getId() + "";
	}

	
	public static ContactSearchResultInstance getEmptyInstance() {
		ContactSearchResultInstance instance = new ContactSearchResultInstance();
		instance.type = ' ';
		return instance;
	}
	
	public static ContactSearchResultInstance getAddNewInstance() {
		ContactSearchResultInstance instance = new ContactSearchResultInstance();
		instance.type = ADD_NEW;
		return instance;
	}
	
	public static ContactSearchResultInstance getNewSearchInstance() {
		ContactSearchResultInstance instance = new ContactSearchResultInstance();
		instance.type = NEW_SEARCH;
		return instance;
	}

	public static BeanModel obtainModel(ContactSearchResultInstance instance) {
		if (beanModelfactory == null)
			beanModelfactory  = BeanModelLookup.get().getFactory(ContactSearchResultInstance.class);
		BeanModel model = beanModelfactory.createModel(instance);
		return model;
	}

	public String toString() {
		return getName();
	}
}
