package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * A tuple of any element of an agreement with a note.
 * 
 * @author Bob Lacatena
 *
 */
public class AgreementNotesTuple implements BeanModelTag, IsSerializable, UserCacheTarget {
	
	
	AgreementInstance			agreement;
	AgreementContactInstance	agreementContact;
	AgreementLinkInstance		agreementLink;
	AgreementSiteInstance		agreementSite;
	AgreementTermInstance		agreementTerm;
	AuthMethodInstance			authMethod;
	RemoteSetupUrlInstance		remoteSetupUrl;

	public AgreementNotesTuple() {
	}
	public AgreementNotesTuple(AgreementInstance agreement) {
		this.agreement		= agreement;
	}
	public AgreementNotesTuple(AgreementInstance agreement, AgreementContactInstance agreementContact) {
		this.agreement		= agreement;
		this.agreementContact	= agreementContact;
	}
	public AgreementNotesTuple(AgreementInstance agreement, AgreementLinkInstance agreementLink) {
		this.agreement		= agreement;
		this.agreementLink	= agreementLink;
	}
	public AgreementNotesTuple(AgreementInstance agreement, AgreementSiteInstance agreementSite) {
		this.agreement		= agreement;
		this.agreementSite	= agreementSite;
	}
	public AgreementNotesTuple(AgreementInstance agreement, AgreementTermInstance agreementTerm) {
		this.agreement		= agreement;
		this.agreementTerm	= agreementTerm;
	}
	public AgreementNotesTuple(AgreementInstance agreement, AuthMethodInstance authMethod) {
		this.agreement		= agreement;
		this.authMethod		= authMethod;
	}
	public AgreementNotesTuple(AgreementInstance agreement, RemoteSetupUrlInstance remoteSetupUrl) {
		this.agreement		= agreement;
		this.remoteSetupUrl		= remoteSetupUrl;
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
	public String getChildType() {
		if (agreementContact != null)
			return "Contact";
		if (agreementLink != null)
			return "Link";
		if (agreementSite != null)
			return "Site";
		if (agreementTerm != null)
			return "Product Term";
		if (authMethod != null) {
			if (AuthMethodInstance.AM_IP.equals(authMethod.getMethodType()))
				return "IP Address";
			if (AuthMethodInstance.AM_UID.equals(authMethod.getMethodType()))
				return "User ID";
			if (AuthMethodInstance.AM_URL.equals(authMethod.getMethodType()))
				return "URL";
			return "Access Method";
		}
		if (remoteSetupUrl != null)
			return "Remote Setup URL";
		
		if (agreement != null)
			return "Agreement";
		
		return "None";
	}
	public String getNoteStart() {
		String note = getChildNote();
		if (note != null && note.length() > 100)
			return note.substring(0, 100) + "...";
		return note;
	}
	public String getChildNote() {
		if (authMethod != null)
			return authMethod.getNote();
		if (remoteSetupUrl != null)
			return remoteSetupUrl.getNote();
		if (agreementContact != null && agreementContact.getContact() != null)
			return agreementContact.getContact().getNote();
		if (agreementLink != null)
			return agreementLink.getNote();
		if (agreementSite != null)
			return agreementSite.getNote();
		if (agreementTerm != null)
			return agreementTerm.getNote();
		
		if (agreement != null)
			return agreement.getNote();
		
		return "";
	}
	public String getKeyDescriptor() {
		if (authMethod != null)
			return authMethod.getMethodDisplay();
		if (remoteSetupUrl != null)
			return remoteSetupUrl.getUrl();
		if (agreementContact != null)
			if (agreementContact.getContact() != null)
				return agreementContact.getContact().getFullName();
			else
				return "ID " + agreementContact.getContactId();
		if (agreementLink != null)
			return "Link #" + AppConstants.appendCheckDigit(agreementLink.getLinkId()) + "";
		if (agreementSite != null)
			if (agreementSite.getSite() != null && agreementSite.getSite().getInstitution() != null)
				return agreementSite.getSite().getInstitution().getInstitutionName();
			else
				return "UCN " + agreementSite.getSiteUcn();
		if (agreementTerm != null)
			if (agreementTerm.getEndDate() != null) {
				return agreementTerm.getProductCode() + " to " + UiConstants.formatDate(agreementTerm.getEndDate());
			} else
				return agreementTerm.getProductCode();
		
		if (agreement != null)
			return AppConstants.appendCheckDigit(agreement.getId()) + "";
		
		return "";
	}
	public String getUniqueKey() {
		if (authMethod != null)
			return authMethod.getUniqueKey();
		if (agreementContact != null && agreementContact.getContact() != null)
			return agreementContact.getUniqueKey();
		if (agreementLink != null)
			return agreementLink.getLinkId() + "";
		if (agreementSite != null)
			return agreementSite.getUniqueKey();
		if (agreementTerm != null)
			return agreementTerm.getUniqueKey();
		if (remoteSetupUrl != null)
			return remoteSetupUrl.getUniqueKey();
		
		if (agreement != null)
			return agreement.getId() + "";
		
		return "";
	}
	public String getAgreementNote() {
		return agreement.getNote();
	}
	
	public AgreementContactInstance getAgreementContact() {
		return agreementContact;
	}
	public void setAgreementContact(AgreementContactInstance agreementContact) {
		this.agreementContact = agreementContact;
	}
	public AgreementLinkInstance getAgreementLink() {
		return agreementLink;
	}
	public void setAgreementLink(AgreementLinkInstance agreementLink) {
		this.agreementLink = agreementLink;
	}
	public AgreementSiteInstance getAgreementSite() {
		return agreementSite;
	}
	public void setAgreementSite(AgreementSiteInstance agreementSite) {
		this.agreementSite = agreementSite;
	}
	public AuthMethodInstance getAuthMethod() {
		return authMethod;
	}
	public void setAuthMethod(AuthMethodInstance authMethod) {
		this.authMethod = authMethod;
	}
	public RemoteSetupUrlInstance getRemoteSetupUrl() {
		return remoteSetupUrl;
	}
	public void setRemoteSetupUrl(RemoteSetupUrlInstance remoteSetupUrl) {
		this.remoteSetupUrl = remoteSetupUrl;
	}
	public static String getUserCacheCategory() {
		return getUserCacheCategory(0);
	}
	
	public static String getUserCacheCategory(int keySet) {
		return "None";
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
		return 0;
	}
	
	@Override
	public int userCacheKeyCount() {
		return 0;	// All access recording is turned off!!!!
	}
	
}
