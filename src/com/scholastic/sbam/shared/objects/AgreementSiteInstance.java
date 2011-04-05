package com.scholastic.sbam.shared.objects;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class AgreementSiteInstance extends BetterRowEditInstance implements BeanModelTag, IsSerializable {

	private int		agreementId;
	private int		siteUcn;
	private int		siteUcnSuffix;
	private String	siteLocCode;
	
	private String	commissionCode;
	
	private Date	activeDate;
	private Date	inactiveDate;
	
	private String	orgPath;
	
	private String	note;

	private String	cancelReasonCode;
	private char	status;
	private boolean	active;
	private Date	createdDatetime;
	
	private SiteInstance			site;
//	private InstitutionInstance		institution;
	private CancelReasonInstance	cancelReason;
	private CommissionTypeInstance	commissionType;
	
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
		this.active = (this.status == 'A');
	}

	public Date getCreatedDatetime() {
		return createdDatetime;
	}

	public void setCreatedDatetime(Date createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		if (this.status == 'X')
			return;
		setStatus(active?'A':'I');
	}

	public int getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(int agreementId) {
		this.agreementId = agreementId;
	}

	public int getSiteUcn() {
		return siteUcn;
	}

	public void setSiteUcn(int siteUcn) {
		this.siteUcn = siteUcn;
	}

	public int getSiteUcnSuffix() {
		return siteUcnSuffix;
	}

	public void setSiteUcnSuffix(int siteUcnSuffix) {
		this.siteUcnSuffix = siteUcnSuffix;
	}

	public String getSiteLocCode() {
		return siteLocCode;
	}

	public void setSiteLocCode(String siteLocCode) {
		this.siteLocCode = siteLocCode;
	}

	public String getCancelReasonCode() {
		return cancelReasonCode;
	}

	public void setCancelReasonCode(String cancelReasonCode) {
		this.cancelReasonCode = cancelReasonCode;
	}

	public String getCommissionCode() {
		return commissionCode;
	}

	public void setCommissionCode(String commissionCode) {
		this.commissionCode = commissionCode;
	}

	public Date getActiveDate() {
		return activeDate;
	}

	public void setActiveDate(Date activeDate) {
		this.activeDate = activeDate;
	}

	public Date getInactiveDate() {
		return inactiveDate;
	}

	public void setInactiveDate(Date inactiveDate) {
		this.inactiveDate = inactiveDate;
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
	}

//	public InstitutionInstance getInstitution() {
//		return institution;
//	}
//
//	public void setInstitution(InstitutionInstance institution) {
//		this.institution = institution;
//	}

	public CancelReasonInstance getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(CancelReasonInstance cancelReason) {
		this.cancelReason = cancelReason;
		if (this.cancelReason == null)
			this.cancelReasonCode = "";
		else
			this.cancelReasonCode = cancelReason.getCancelReasonCode();
	}

	public CommissionTypeInstance getCommissionType() {
		return commissionType;
	}

	public void setCommissionType(CommissionTypeInstance commissionType) {
		this.commissionType = commissionType;
		if (this.commissionType == null)
			this.commissionCode = "";
		else
			this.commissionCode = commissionType.getCommissionCode();
	}
	
	public String getDisplayUcn() {
		if (siteUcnSuffix <= 0)
			return siteUcn + "";
		return siteUcn + " - " + siteUcnSuffix;
	}
	
	public void setValuesFrom(AgreementSiteInstance fromInstance) {
		this.agreementId				= fromInstance.agreementId;
		this.siteUcn					= fromInstance.siteUcn;
		this.siteUcnSuffix				= fromInstance.siteUcnSuffix;
		this.siteLocCode				= fromInstance.siteLocCode;
		
		this.commissionCode				= fromInstance.commissionCode;
		
		this.activeDate					= fromInstance.activeDate;
		this.inactiveDate				= fromInstance.inactiveDate;
		
		this.orgPath					= fromInstance.orgPath;
		
		this.note						= fromInstance.note;

		this.cancelReasonCode			= fromInstance.cancelReasonCode;
		this.status						= fromInstance.status;
		this.active						= fromInstance.active;
		this.createdDatetime			= fromInstance.createdDatetime;
		
		this.site						= fromInstance.site;
//		this.institution				= fromInstance.institution;
		this.cancelReason				= fromInstance.cancelReason;
		this.commissionType				= fromInstance.commissionType;
	}
	
	public String getUniqueKey() {
		return agreementId + ":" + siteUcn + ":" + siteUcnSuffix + ":" + siteLocCode;
	}

	public String toString() {
		return "Site " + agreementId + "-" + siteUcn + "-" + siteUcnSuffix + " - " + siteLocCode;
	}
}
