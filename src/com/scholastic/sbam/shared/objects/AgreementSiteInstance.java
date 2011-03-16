package com.scholastic.sbam.shared.objects;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class AgreementSiteInstance extends BetterRowEditInstance implements BeanModelTag, IsSerializable {

	private int		agreementId;
	private int		site_ucn;
	private int		site_ucn_suffix;
	
	private String	commissionCode;
	private String	commissionCodeDescription;
	
	private Date	activeDate;
	private Date	inactiveDate;
	
	private String	orgPath;
	
	private String	note;
	
	private char	status;
	private boolean	active;
	private Date	createdDatetime;
	
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

	public int getSite_ucn() {
		return site_ucn;
	}

	public void setSite_ucn(int site_ucn) {
		this.site_ucn = site_ucn;
	}

	public int getSite_ucn_suffix() {
		return site_ucn_suffix;
	}

	public void setSite_ucn_suffix(int site_ucn_suffix) {
		this.site_ucn_suffix = site_ucn_suffix;
	}

	public String getCommissionCode() {
		return commissionCode;
	}

	public void setCommissionCode(String commissionCode) {
		this.commissionCode = commissionCode;
	}

	public String getCommissionCodeDescription() {
		return commissionCodeDescription;
	}

	public void setCommissionCodeDescription(String commissionCodeDescription) {
		this.commissionCodeDescription = commissionCodeDescription;
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

	public String toString() {
		return "Site " + agreementId + "-" + site_ucn + "-" + site_ucn_suffix;
	}
}
