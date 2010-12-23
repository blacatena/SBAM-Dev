package com.scholastic.sbam.server.database.codegen;

// Generated Dec 22, 2010 4:10:32 PM by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * PreferenceCode generated by hbm2java
 */
public class PreferenceCode implements java.io.Serializable {

	private PreferenceCodeId id;
	private String description;
	private int seq;
	private String exportValue;
	private Date createdDatetime;
	private char status;

	public PreferenceCode() {
	}

	public PreferenceCode(PreferenceCodeId id, String description, int seq,
			String exportValue, Date createdDatetime, char status) {
		this.id = id;
		this.description = description;
		this.seq = seq;
		this.exportValue = exportValue;
		this.createdDatetime = createdDatetime;
		this.status = status;
	}

	public PreferenceCodeId getId() {
		return this.id;
	}

	public void setId(PreferenceCodeId id) {
		this.id = id;
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

	public String getExportValue() {
		return this.exportValue;
	}

	public void setExportValue(String exportValue) {
		this.exportValue = exportValue;
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