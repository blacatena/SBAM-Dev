package com.scholastic.sbam.server.database.codegen;

// Generated Jan 19, 2011 12:27:01 PM by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * Agreement generated by hbm2java
 */
public class Agreement implements java.io.Serializable {

	private Integer id;
	private char idCheckDigit;
	private int billUcn;
	private String agreementTypeCode;
	private String deleteReasonCode;
	private Date createdDatetime;
	private char status;

	public Agreement() {
	}

	public Agreement(char idCheckDigit, int billUcn, String agreementTypeCode,
			String deleteReasonCode, Date createdDatetime, char status) {
		this.idCheckDigit = idCheckDigit;
		this.billUcn = billUcn;
		this.agreementTypeCode = agreementTypeCode;
		this.deleteReasonCode = deleteReasonCode;
		this.createdDatetime = createdDatetime;
		this.status = status;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public char getIdCheckDigit() {
		return this.idCheckDigit;
	}

	public void setIdCheckDigit(char idCheckDigit) {
		this.idCheckDigit = idCheckDigit;
	}

	public int getBillUcn() {
		return this.billUcn;
	}

	public void setBillUcn(int billUcn) {
		this.billUcn = billUcn;
	}

	public String getAgreementTypeCode() {
		return this.agreementTypeCode;
	}

	public void setAgreementTypeCode(String agreementTypeCode) {
		this.agreementTypeCode = agreementTypeCode;
	}

	public String getDeleteReasonCode() {
		return this.deleteReasonCode;
	}

	public void setDeleteReasonCode(String deleteReasonCode) {
		this.deleteReasonCode = deleteReasonCode;
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
