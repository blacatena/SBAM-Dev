package com.scholastic.sbam.shared.objects;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.scholastic.sbam.shared.util.AppConstants;

public class RemoteSetupUrlInstance extends IpAddressInstance implements BeanModelTag, IsSerializable {
	
	private static BeanModelFactory beanModelfactory;
	private int		agreementId;
	private int		ucn;
	private int		ucnSuffix;
	private String	siteLocCode;
	private int		urlId;
	
	private String	url;

	private int		forUcn;
	private int		forUcnSuffix;
	private String	forSiteLocCode;
	
	private char	approved;
	private char	activated;
	
	private String	orgPath;
	
	private String	note;

	private char	status;
	private Date	createdDatetime;
	
	private SiteInstance site;
	
	@Override
	public void markForDeletion() {
		setStatus('X');
	}

	@Override
	public boolean thisIsDeleted() {
		return status == 'X';
	}

	@Override
	public boolean thisIsValid() {
		return true;
	}

	@Override
	public String returnTriggerProperty() {
		return "junk";
	}

	@Override
	public String returnTriggerValue() {
		return "junk";
	}

	public char getStatus() {
		return status;
	}

	public void setStatus(char status) {
		this.status = status;
	}

	public Date getCreatedDatetime() {
		return createdDatetime;
	}

	public void setCreatedDatetime(Date createdDatetime) {
		this.createdDatetime = createdDatetime;
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

	public int getUrlId() {
		return urlId;
	}

	public void setUrlId(int urlId) {
		this.urlId = urlId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
//		syncMethodKey();
	}

	public int getForUcn() {
		return forUcn;
	}

	public void setForUcn(int forUcn) {
		this.forUcn = forUcn;
	}

	public int getForUcnSuffix() {
		return forUcnSuffix;
	}

	public void setForUcnSuffix(int forUcnSuffix) {
		this.forUcnSuffix = forUcnSuffix;
	}

	public String getForSiteLocCode() {
		return forSiteLocCode;
	}

	public void setForSiteLocCode(String forSiteLocCode) {
		this.forSiteLocCode = forSiteLocCode;
	}

	public char getApproved() {
		return approved;
	}

	public void setApproved(char approved) {
		this.approved = approved;
	}
	
	public boolean isApproved() {
		return (approved == 'y' || approved == 'Y');
	}

	public char getActivated() {
		return activated;
	}
	
	public boolean isActivated() {
		return (activated == 'y' || activated == 'Y');
	}

	public void setActivated(char activated) {
		this.activated = activated;
	}

	public String getOrgPath() {
		return orgPath;
	}

	public void setOrgPath(String orgPath) {
		this.orgPath = orgPath;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public SiteInstance getSite() {
		return site;
	}

	public void setSite(SiteInstance site) {
		this.site = site;
		this.setForUcn(site.getUcn());
		this.setForUcnSuffix(site.getUcnSuffix());
		this.setForSiteLocCode(site.getSiteLocCode());
	}
	
	public String getStatusDescription() {
		return AppConstants.getStatusDescription(status);
	}
	
	public MethodIdInstance obtainMethodId() {
		MethodIdInstance mid = new MethodIdInstance();
		mid.setAgreementId(agreementId);
		mid.setUcn(ucn);
		mid.setUcnSuffix(ucnSuffix);
		mid.setSiteLocCode(siteLocCode);
		mid.setMethodType("rsurl");
		mid.setMethodKey(urlId);
		mid.setForUcn(forUcn);
		mid.setForUcnSuffix(forUcnSuffix);
		mid.setForSiteLocCode(forSiteLocCode);
		mid.setProxyId(0);
		mid.setIpId(0);
		return mid;
	}
	
	public void setValuesFrom(RemoteSetupUrlInstance fromInstance) {
		this.agreementId				=	fromInstance.agreementId;
		this.ucn						=	fromInstance.ucn;
		this.ucnSuffix					=	fromInstance.ucnSuffix;
		this.siteLocCode				=	fromInstance.siteLocCode;
		this.urlId						=	fromInstance.urlId;
				
		this.url						=	fromInstance.	url;
		
		this.forUcn						=	fromInstance.	forUcn;
		this.forUcnSuffix				=	fromInstance.	forUcnSuffix;
		this.forSiteLocCode				=	fromInstance.	forSiteLocCode;
		
		this.approved					=	fromInstance.	approved;
		this.activated					=	fromInstance.	activated;
	
		this.orgPath					=	fromInstance.	orgPath;
			
		this.note						=	fromInstance.	note;
	
		this.status						=	fromInstance.	status;
		this.createdDatetime			=	fromInstance.	createdDatetime;
		
		this.site						=	fromInstance.	site;
	}
	
	public String getUniqueKey() {
		return agreementId + ":" + ucn + ":" + ucnSuffix + ":" + siteLocCode + ":" + urlId;
	}

	public static BeanModel obtainModel(RemoteSetupUrlInstance instance) {
		if (beanModelfactory == null)
			beanModelfactory  = BeanModelLookup.get().getFactory(RemoteSetupUrlInstance.class);
		BeanModel model = beanModelfactory.createModel(instance);
		return model;
	}

	public String toString() {
		return getUniqueKey();
	}
}
