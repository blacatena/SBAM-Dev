package com.scholastic.sbam.server.database.codegen;

// Generated Jul 19, 2011 10:00:16 AM by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * SitePreference generated by hbm2java
 */
public class SitePreference implements java.io.Serializable {

	private SitePreferenceId id;
	private String prefSelCode;
	private Date createdDatetime;
	private char status;

	public SitePreference() {
	}

	public SitePreference(SitePreferenceId id, String prefSelCode,
			Date createdDatetime, char status) {
		this.id = id;
		this.prefSelCode = prefSelCode;
		this.createdDatetime = createdDatetime;
		this.status = status;
	}

	public SitePreferenceId getId() {
		return this.id;
	}

	public void setId(SitePreferenceId id) {
		this.id = id;
	}

	public String getPrefSelCode() {
		return this.prefSelCode;
	}

	public void setPrefSelCode(String prefSelCode) {
		this.prefSelCode = prefSelCode;
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
