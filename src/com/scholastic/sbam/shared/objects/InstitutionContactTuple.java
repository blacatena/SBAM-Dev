package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A tuple of an institution term with its parent institution.
 * 
 * @author Bob Lacatena
 *
 */
public class InstitutionContactTuple implements BeanModelTag, IsSerializable, UserCacheTarget {
	
	InstitutionInstance			institution;
	InstitutionContactInstance		institutionContact;

	public InstitutionContactTuple() {
	}
	public InstitutionContactTuple(InstitutionInstance institution, InstitutionContactInstance institutionContact) {
		this.institution			= institution;
		this.institutionContact	= institutionContact;
	}
	public InstitutionInstance getInstitution() {
		return institution;
	}
	public void setInstitution(InstitutionInstance institution) {
		this.institution = institution;
	}
	public InstitutionContactInstance getInstitutionContact() {
		return institutionContact;
	}
	public void setInstitutionContact(InstitutionContactInstance institutionContact) {
		this.institutionContact = institutionContact;
	}
	public ContactInstance getContact() {
		return institutionContact.getContact();
	}
	public String getContactNote() {
		return institutionContact.getContact().getNote();
	}
	
	public static String getUserCacheCategory() {
		return getUserCacheCategory(0);
	}
	
	public static String getUserCacheCategory(int keySet) {
		return "Institution";
	}

	@Override
	public String userCacheCategory(int keySet) {
		return getUserCacheCategory(keySet);
	}

	@Override
	public String userCacheStringKey(int keySet) {
		return institution.getUcn() + ":" + institutionContact.getContactId();
	}

	@Override
	public int userCacheIntegerKey(int keySet) {
		return 0;
	}
	
	@Override
	public int userCacheKeyCount() {
		return 0;	// All access recording is turned off!!!!
	}
	
}
