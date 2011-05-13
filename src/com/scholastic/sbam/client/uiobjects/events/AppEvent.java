package com.scholastic.sbam.client.uiobjects.events;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.objects.AgreementSiteInstance;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.SiteInstance;

public class AppEvent extends BaseEvent {
	
	int	agreementId;
	int ucn;
	int ucnSuffix;
	String siteLocCode;

	public AppEvent(EventType eventType) {
		super(eventType);
	}

	public int getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(int agreementId) {
		this.agreementId = agreementId;
	}

	public int getUcn() {
		return ucn;
	}

	public void setUcn(int ucn) {
		this.ucn = ucn;
	}

	public int getUcnSuffix() {
		return ucnSuffix;
	}

	public void setUcnSuffix(int ucnSuffix) {
		this.ucnSuffix = ucnSuffix;
	}

	public String getSiteLocCode() {
		return siteLocCode;
	}

	public void setSiteLocCode(String siteLocCode) {
		this.siteLocCode = siteLocCode;
	}

	public void set(SiteInstance site) {
		setUcn(site.getUcn());
		setUcnSuffix(site.getUcnSuffix());
		setSiteLocCode(site.getSiteLocCode());
	}

	public void set(AgreementSiteInstance agreementSite) {
		setUcn(agreementSite.getSiteUcn());
		setUcnSuffix(agreementSite.getSiteUcnSuffix());
		setSiteLocCode(agreementSite.getSiteLocCode());
	}

	public void set(InstitutionInstance institution) {
		setUcn(institution.getUcn());
	}

	public void set(AgreementInstance agreement) {
		setAgreementId(agreement.getId());
		setUcn(agreement.getBillUcn());
		setUcnSuffix(agreement.getBillUcnSuffix());
	}

	public void set(AgreementTermInstance agreement) {
		setAgreementId(agreement.getAgreementId());
	}
}
