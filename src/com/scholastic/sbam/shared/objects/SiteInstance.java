package com.scholastic.sbam.shared.objects;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class SiteInstance extends BetterRowEditInstance implements BeanModelTag, IsSerializable {

	private int		ucn;
	private int		ucnSuffix;
	
	private String	siteLocCode;
	private String	description;
	
	private String	commissionCode;
	private String	commissionCodeDescription;
	
	private char	pseudoSite;
	
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public char getPseudoSite() {
		return pseudoSite;
	}

	public void setPseudoSite(char pseudoSite) {
		this.pseudoSite = pseudoSite;
	}

	public boolean isAPseudoSite() {
		return pseudoSite == 'y';
	}

	public void setAPseudoSite(boolean pseudoSite) {
		this.pseudoSite = pseudoSite ? 'y' : 'n';
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

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String toString() {
		return "Site " + ucn + "-" + ucnSuffix;
	}
}
