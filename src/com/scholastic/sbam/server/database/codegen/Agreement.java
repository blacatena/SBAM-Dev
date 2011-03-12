package com.scholastic.sbam.server.database.codegen;

// Generated Mar 12, 2011 2:07:15 PM by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * Agreement generated by hbm2java
 */
public class Agreement implements java.io.Serializable {

	private Integer id;
	private int idCheckDigit;
	private int billUcn;
	private int billUcnSuffix;
	private String agreementTypeCode;
	private String commissionCode;
	private String deleteReasonCode;
	private String orgPath;
	private Date createdDatetime;
	private char status;
	private String note;

	public Agreement() {
	}

	public Agreement(int idCheckDigit, int billUcn, int billUcnSuffix,
			String agreementTypeCode, String commissionCode,
			String deleteReasonCode, String orgPath, Date createdDatetime,
			char status, String note) {
		this.idCheckDigit = idCheckDigit;
		this.billUcn = billUcn;
		this.billUcnSuffix = billUcnSuffix;
		this.agreementTypeCode = agreementTypeCode;
		this.commissionCode = commissionCode;
		this.deleteReasonCode = deleteReasonCode;
		this.orgPath = orgPath;
		this.createdDatetime = createdDatetime;
		this.status = status;
		this.note = note;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getIdCheckDigit() {
		return this.idCheckDigit;
	}

	public void setIdCheckDigit(int idCheckDigit) {
		this.idCheckDigit = idCheckDigit;
	}

	public int getBillUcn() {
		return this.billUcn;
	}

	public void setBillUcn(int billUcn) {
		this.billUcn = billUcn;
	}

	public int getBillUcnSuffix() {
		return this.billUcnSuffix;
	}

	public void setBillUcnSuffix(int billUcnSuffix) {
		this.billUcnSuffix = billUcnSuffix;
	}

	public String getAgreementTypeCode() {
		return this.agreementTypeCode;
	}

	public void setAgreementTypeCode(String agreementTypeCode) {
		this.agreementTypeCode = agreementTypeCode;
	}

	public String getCommissionCode() {
		return this.commissionCode;
	}

	public void setCommissionCode(String commissionCode) {
		this.commissionCode = commissionCode;
	}

	public String getDeleteReasonCode() {
		return this.deleteReasonCode;
	}

	public void setDeleteReasonCode(String deleteReasonCode) {
		this.deleteReasonCode = deleteReasonCode;
	}

	public String getOrgPath() {
		return this.orgPath;
	}

	public void setOrgPath(String orgPath) {
		this.orgPath = orgPath;
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

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
