package com.scholastic.sbam.shared.objects;

import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A tuple of an agreement link with its related agreements.
 * 
 * @author Bob Lacatena
 *
 */
public class AgreementLinkTuple implements BeanModelTag, IsSerializable, UserCacheTarget {
	
	public static final int AGREEMENT_LINK_KEY_SET		=	0;
	
	AgreementLinkInstance	agreementLink;
	List<AgreementInstance>	agreements;

	public AgreementLinkTuple() {
	}
	public AgreementLinkTuple(AgreementLinkInstance agreementLink, List<AgreementInstance> agreements) {
		this.agreements		= agreements;
		this.agreementLink	= agreementLink;
	}
	public AgreementLinkInstance getAgreementLink() {
		return agreementLink;
	}
	public void setAgreementLink(AgreementLinkInstance agreementLink) {
		this.agreementLink = agreementLink;
	}
	public List<AgreementInstance> getAgreements() {
		return agreements;
	}
	public void setAgreements(List<AgreementInstance> agreements) {
		this.agreements = agreements;
	}
	public String getAgreementLinkNote() {
		return agreementLink.getNote();
	}
	
	public static String getUserCacheCategory() {
		return getUserCacheCategory(0);
	}
	
	public static String getUserCacheCategory(int keySet) {
		return "AgreementLink";
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
		return agreementLink.getLinkId();
	}
	
	@Override
	public int userCacheKeyCount() {
		return 0;	// All access recording is turned off!!!!
	}
	
}
