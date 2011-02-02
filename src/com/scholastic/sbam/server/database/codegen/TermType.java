package com.scholastic.sbam.server.database.codegen;

// Generated Jan 31, 2011 3:49:15 PM by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * TermType generated by hbm2java
 */
public class TermType implements java.io.Serializable {

	private String termTypeCode;
	private String description;
	private char activate;
	private Date createdDatetime;
	private char status;

	public TermType() {
	}

	public TermType(String termTypeCode, String description, char activate,
			Date createdDatetime, char status) {
		this.termTypeCode = termTypeCode;
		this.description = description;
		this.activate = activate;
		this.createdDatetime = createdDatetime;
		this.status = status;
	}

	public String getTermTypeCode() {
		return this.termTypeCode;
	}

	public void setTermTypeCode(String termTypeCode) {
		this.termTypeCode = termTypeCode;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public char getActivate() {
		return this.activate;
	}

	public void setActivate(char activate) {
		this.activate = activate;
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
