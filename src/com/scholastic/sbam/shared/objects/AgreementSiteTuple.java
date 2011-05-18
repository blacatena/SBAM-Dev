package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A tuple of an agreement term with its parent agreement.
 * 
 * @author Bob Lacatena
 *
 */
public class AgreementSiteTuple implements BeanModelTag, IsSerializable, UserCacheTarget {
	
	public static final int AGREEMENT_KEY_SET		=	0;
//	public static final int AGREEMENT_SITE_KEY_SET	=	1;
	
	AgreementInstance		agreement;
	AgreementSiteInstance	agreementSite;

	public AgreementSiteTuple() {
	}
	public AgreementSiteTuple(AgreementInstance agreement, AgreementSiteInstance agreementSite) {
		this.agreement		= agreement;
		this.agreementSite	= agreementSite;
	}
	public AgreementInstance getAgreement() {
		return agreement;
	}
	public void setAgreement(AgreementInstance agreement) {
		this.agreement = agreement;
	}
	public AgreementSiteInstance getAgreementSite() {
		return agreementSite;
	}
	public void setAgreementSite(AgreementSiteInstance agreementSite) {
		this.agreementSite = agreementSite;
	}
	public String getAgreementSiteNote() {
		return agreementSite.getNote();
	}
	public String getAgreementNote() {
		return agreement.getNote();
	}
	public String getSiteNote() {
		return agreementSite.getSite().getNote();
	}
	
	public static String getUserCacheCategory() {
		return getUserCacheCategory(0);
	}
	
	public static String getUserCacheCategory(int keySet) {
//		if (keySet == AGREEMENT_SITE_KEY_SET)
//			return "AgreementSite";
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
//		if (keySet == AGREMENT_SITE_KEY_SET)
//			return agreementSite.getSiteId();
		return agreement.getId();
	}
	
	@Override
	public int userCacheKeyCount() {
		return 0;	// All access recording is turned off!!!!
	}
	
}
