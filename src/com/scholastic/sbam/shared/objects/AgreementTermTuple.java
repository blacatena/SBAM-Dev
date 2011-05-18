package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A tuple of an agreement term with its parent agreement.
 * 
 * @author Bob Lacatena
 *
 */
public class AgreementTermTuple implements BeanModelTag, IsSerializable, UserCacheTarget {
	
	public static final int AGREEMENT_KEY_SET		=	0;
//	public static final int AGREEMENT_TERM_KEY_SET	=	1;
	
	AgreementInstance		agreement;
	AgreementTermInstance	agreementTerm;

	public AgreementTermTuple() {
	}
	public AgreementTermTuple(AgreementInstance agreement, AgreementTermInstance agreementTerm) {
		this.agreement		= agreement;
		this.agreementTerm	= agreementTerm;
	}
	public AgreementInstance getAgreement() {
		return agreement;
	}
	public void setAgreement(AgreementInstance agreement) {
		this.agreement = agreement;
	}
	public AgreementTermInstance getAgreementTerm() {
		return agreementTerm;
	}
	public void setAgreementTerm(AgreementTermInstance agreementTerm) {
		this.agreementTerm = agreementTerm;
	}
	
	public static String getUserCacheCategory() {
		return getUserCacheCategory(0);
	}
	
	public static String getUserCacheCategory(int keySet) {
//		if (keySet == AGREEMENT_TERM_KEY_SET)
//			return "AgreementTerm";
		return "AgreementTerm";
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
//			return agreementTerm.getTermId();
		return agreement.getId();
	}
	
	@Override
	public int userCacheKeyCount() {
		return 1;	// AGREEMENT_TERM_SET_KEY is turned off!!!!
	}
	
}
