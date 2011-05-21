package com.scholastic.sbam.server.database.codegen;

// Generated May 20, 2011 5:59:16 PM by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * AgreementSite generated by hbm2java
 */
public class AgreementSite implements java.io.Serializable {

	private AgreementSiteId id;
	private String commissionCode;
	private String orgPath;
	private String cancelReasonCode;
	private Date activeDate;
	private Date inactiveDate;
	private String note;
	private Date createdDatetime;
	private char status;

	public AgreementSite() {
	}

	public AgreementSite(AgreementSiteId id, String commissionCode,
			String orgPath, String cancelReasonCode, String note,
			Date createdDatetime, char status) {
		this.id = id;
		this.commissionCode = commissionCode;
		this.orgPath = orgPath;
		this.cancelReasonCode = cancelReasonCode;
		this.note = note;
		this.createdDatetime = createdDatetime;
		this.status = status;
	}

	public AgreementSite(AgreementSiteId id, String commissionCode,
			String orgPath, String cancelReasonCode, Date activeDate,
			Date inactiveDate, String note, Date createdDatetime, char status) {
		this.id = id;
		this.commissionCode = commissionCode;
		this.orgPath = orgPath;
		this.cancelReasonCode = cancelReasonCode;
		this.activeDate = activeDate;
		this.inactiveDate = inactiveDate;
		this.note = note;
		this.createdDatetime = createdDatetime;
		this.status = status;
	}

	public AgreementSiteId getId() {
		return this.id;
	}

	public void setId(AgreementSiteId id) {
		this.id = id;
	}

	public String getCommissionCode() {
		return this.commissionCode;
	}

	public void setCommissionCode(String commissionCode) {
		this.commissionCode = commissionCode;
	}

	public String getOrgPath() {
		return this.orgPath;
	}

	public void setOrgPath(String orgPath) {
		this.orgPath = orgPath;
	}

	public String getCancelReasonCode() {
		return this.cancelReasonCode;
	}

	public void setCancelReasonCode(String cancelReasonCode) {
		this.cancelReasonCode = cancelReasonCode;
	}

	public Date getActiveDate() {
		return this.activeDate;
	}

	public void setActiveDate(Date activeDate) {
		this.activeDate = activeDate;
	}

	public Date getInactiveDate() {
		return this.inactiveDate;
	}

	public void setInactiveDate(Date inactiveDate) {
		this.inactiveDate = inactiveDate;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Date getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Date createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public char getStatus() {
		return this.status;
	}

	public void setStatus(char status) {
		this.status = status;
	}

}
