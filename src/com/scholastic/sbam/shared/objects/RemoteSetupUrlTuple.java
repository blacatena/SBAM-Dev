package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * A tuple of an agreement term with its parent agreement.
 * 
 * @author Bob Lacatena
 *
 */
public class RemoteSetupUrlTuple implements BeanModelTag, IsSerializable, UserCacheTarget {
	
	public static final int AGREEMENT_KEY_SET		=	0;
//	public static final int AGREEMENT_TERM_KEY_SET	=	1;
	
	AgreementInstance			agreement;
	RemoteSetupUrlInstance		remoteSetupUrl;
	SiteInstance				owningSite;

	public RemoteSetupUrlTuple() {
	}
	public RemoteSetupUrlTuple(AgreementInstance agreement, RemoteSetupUrlInstance remoteSetupUrl) {
		this.agreement	= agreement;
		this.remoteSetupUrl	= remoteSetupUrl;
		this.owningSite = null;
	}
	public RemoteSetupUrlTuple(AgreementInstance agreement, SiteInstance owningSite) {
		this.agreement	= agreement;
		this.remoteSetupUrl	= null;
		this.owningSite = owningSite;
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
	public void setRemoteSetupUrl(RemoteSetupUrlInstance remoteSetupUrl) {
		this.remoteSetupUrl = remoteSetupUrl;
	}
	public SiteInstance getOwningSite() {
		return owningSite;
	}
	public void setOwningSite(SiteInstance owningSite) {
		this.owningSite = owningSite;
	}
	public String getRemoteSetupUrlNote() {
		if (remoteSetupUrl == null)
			return "";
		return remoteSetupUrl.getNote();
	}
	public String getAgreementNote() {
		if (agreement == null)
			return "";
		return agreement.getNote();
	}
	public String getOwningSiteNote() {
		if (owningSite == null)
			return "";
		return owningSite.getNote();
	}
	/**
	 * Returns the key string for the owning entity, without identifying it's type
	 * @return
	 */
	public String getUnqualifiedOwnerKey() {
		if (agreement != null)
			return "" + AppConstants.appendCheckDigit(agreement.getId());
		if (owningSite != null)
			return "" + owningSite.getUcn() + "-" + owningSite.getUcnSuffix() + " : " + owningSite.getSiteLocCode();
		if (remoteSetupUrl != null)
			return remoteSetupUrl.getUniqueKey();
		return "None";
	}
	/**
	 * Returns a clear string identifying the owning entity.
	 * @return
	 */
	public String getOwnerKey() {
		if (agreement != null)
			return "Agreement " + AppConstants.appendCheckDigit(agreement.getId());
		if (owningSite != null)
			return "Site " + owningSite.getUcn() + "-" + owningSite.getUcnSuffix() + " : " + owningSite.getSiteLocCode();
		if (remoteSetupUrl != null)
			return "Unknown " + remoteSetupUrl.getUniqueKey();
		return "No data";
	}
	
	public static String getUserCacheCategory() {
		return getUserCacheCategory(0);
	}
	
	public static String getUserCacheCategory(int keySet) {
//		if (keySet == AGREEMENT_TERM_KEY_SET)
//			return "RemoteSetupUrl";
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
//			return RemoteSetupUrl.getTermId();
		return agreement.getId();
	}
	
	@Override
	public int userCacheKeyCount() {
		return 0;	// All access recording is turned off!!!!
	}
	
}
