package com.scholastic.sbam.server.database.codegen;

// Generated Jul 1, 2011 1:29:56 PM by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * ContactType generated by hbm2java
 */
public class ContactType implements java.io.Serializable {

	private String contactTypeCode;
	private String description;
	private char status;
	private Date createdDatetime;

	public ContactType() {
	}

	public ContactType(String contactTypeCode, String description, char status,
			Date createdDatetime) {
		this.contactTypeCode = contactTypeCode;
		this.description = description;
		this.status = status;
		this.createdDatetime = createdDatetime;
	}

	public String getContactTypeCode() {
		return this.contactTypeCode;
	}

	public void setContactTypeCode(String contactTypeCode) {
		this.contactTypeCode = contactTypeCode;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public char getStatus() {
		return this.status;
	}

	public void setStatus(char status) {
		this.status = status;
	}

	public Date getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Date createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

}
