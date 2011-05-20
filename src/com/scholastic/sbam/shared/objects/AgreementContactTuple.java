package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A tuple of an agreement term with its parent agreement.
 * 
 * @author Bob Lacatena
 *
 */
public class AgreementContactTuple implements BeanModelTag, IsSerializable, UserCacheTarget {
	
	public static final int AGREEMENT_KEY_SET		=	0;
//	public static final int AGREEMENT_TERM_KEY_SET	=	1;
	
	AgreementInstance			agreement;
	AgreementContactInstance	agreementContact;

	public AgreementContactTuple() {
	}
	public AgreementContactTuple(AgreementInstance agreement, AgreementContactInstance agreementContact) {
		this.agreement		= agreement;
		this.agreementContact	= agreementContact;
	}
	public AgreementInstance getAgreement() {
		return agreement;
	}
	public void setAgreement(AgreementInstance agreement) {
		this.agreement = agreement;
	}
	public AgreementContactInstance getAgreementContact() {
		return agreementContact;
	}
	public void setAgreementContact(AgreementContactInstance agreementContact) {
		this.agreementContact = agreementContact;
	}
	public String getContactNote() {
		return agreementContact.getContact().getNote();
	}
	public String getAgreementNote() {
		return agreement.getNote();
	}
	
	public static String getUserCacheCategory() {
		return getUserCacheCategory(0);
	}
	
	public static String getUserCacheCategory(int keySet) {
//		if (keySet == AGREEMENT_TERM_KEY_SET)
//			return "AgreementContact";
		return "Agreement";
	}

	@Override
	public String userCacheCategory(int keySet) {
		return getUserCacheCategory(keySet);
	}

	@Override
	public String userCacheStringKey(int keySet) {
		return null;
	}

	@Override
	public int userCacheIntegerKey(int keySet) {
//		if (keySet == AGREMENT_TERM_KEY_SET)
//			return agreementContact.getContactId();
		return agreement.getId();
	}
	
	@Override
	public int userCacheKeyCount() {
		return 0;	// All access recording is turned off!!!!
	}
	
}
