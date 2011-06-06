package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A tuple of an remote setup URL with its parent agreement.
 * 
 * @author Bob Lacatena
 *
 */
public class AgreementRemoteSetupUrlTuple implements BeanModelTag, IsSerializable, UserCacheTarget {
	
	public static final int AGREEMENT_KEY_SET		=	0;
//	public static final int AGREEMENT_TERM_KEY_SET	=	1;
	
	AgreementInstance			agreement;
	RemoteSetupUrlInstance		remoteSetupUrl;

	public AgreementRemoteSetupUrlTuple() {
	}
	public AgreementRemoteSetupUrlTuple(AgreementInstance agreement, RemoteSetupUrlInstance agreementRemoteSetupUrl) {
		this.agreement		= agreement;
		this.remoteSetupUrl	= agreementRemoteSetupUrl;
	}
	public AgreementInstance getAgreement() {
		return agreement;
	}
	public void setAgreement(AgreementInstance agreement) {
		this.agreement = agreement;
	}
	public RemoteSetupUrlInstance getRemoteSetupUrl() {
		return remoteSetupUrl;
	}
	public void setRemoteSetupUrl(RemoteSetupUrlInstance agreementRemoteSetupUrl) {
		this.remoteSetupUrl = agreementRemoteSetupUrl;
	}
	public String getRemoteSetupUrlNote() {
		return remoteSetupUrl.getNote();
	}
	public String getAgreementNote() {
		return agreement.getNote();
	}
	
	public static String getUserCacheCategory() {
		return getUserCacheCategory(0);
	}
	
	public static String getUserCacheCategory(int keySet) {
//		if (keySet == AGREEMENT_TERM_KEY_SET)
//			return "AgreementRemoteSetupUrl";
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
//			return agreementRemoteSetupUrl.getRemoteSetupUrlId();
		return agreement.getId();
	}
	
	@Override
	public int userCacheKeyCount() {
		return 0;	// All access recording is turned off!!!!
	}
	
}
