package com.scholastic.sbam.server.database.codegen;

// Generated Jul 19, 2011 10:00:16 AM by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * PreferenceCategory generated by hbm2java
 */
public class PreferenceCategory implements java.io.Serializable {

	private String prefCatCode;
	private String description;
	private int seq;
	private Date createdDatetime;
	private char status;

	public PreferenceCategory() {
	}

	public PreferenceCategory(String prefCatCode, String description, int seq,
			Date createdDatetime, char status) {
		this.prefCatCode = prefCatCode;
		this.description = description;
		this.seq = seq;
		this.createdDatetime = createdDatetime;
		this.status = status;
	}

	public String getPrefCatCode() {
		return this.prefCatCode;
	}

	public void setPrefCatCode(String prefCatCode) {
		this.prefCatCode = prefCatCode;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getSeq() {
		return this.seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
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
