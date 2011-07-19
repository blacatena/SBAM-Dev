package com.scholastic.sbam.server.database.codegen;

// Generated Jul 19, 2011 10:00:16 AM by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * AgreementType generated by hbm2java
 */
public class AgreementType implements java.io.Serializable {

	private String agreementTypeCode;
	private String description;
	private String shortName;
	private Date createdDatetime;
	private char status;

	public AgreementType() {
	}

	public AgreementType(String agreementTypeCode, String description,
			String shortName, Date createdDatetime, char status) {
		this.agreementTypeCode = agreementTypeCode;
		this.description = description;
		this.shortName = shortName;
		this.createdDatetime = createdDatetime;
		this.status = status;
	}

	public String getAgreementTypeCode() {
		return this.agreementTypeCode;
	}

	public void setAgreementTypeCode(String agreementTypeCode) {
		this.agreementTypeCode = agreementTypeCode;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getShortName() {
		return this.shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
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
