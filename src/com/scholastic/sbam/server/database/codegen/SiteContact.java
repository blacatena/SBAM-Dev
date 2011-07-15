package com.scholastic.sbam.server.database.codegen;

// Generated Jul 14, 2011 10:05:06 PM by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * SiteContact generated by hbm2java
 */
public class SiteContact implements java.io.Serializable {

	private SiteContactId id;
	private Date createdDatetime;
	private char status;

	public SiteContact() {
	}

	public SiteContact(SiteContactId id, Date createdDatetime, char status) {
		this.id = id;
		this.createdDatetime = createdDatetime;
		this.status = status;
	}

	public SiteContactId getId() {
		return this.id;
	}

	public void setId(SiteContactId id) {
		this.id = id;
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
