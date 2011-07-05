package com.scholastic.sbam.server.database.codegen;

// Generated Jul 4, 2011 7:39:15 PM by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * InstitutionContact generated by hbm2java
 */
public class InstitutionContact implements java.io.Serializable {

	private InstitutionContactId id;
	private Date createdDatetime;
	private char status;

	public InstitutionContact() {
	}

	public InstitutionContact(InstitutionContactId id, Date createdDatetime,
			char status) {
		this.id = id;
		this.createdDatetime = createdDatetime;
		this.status = status;
	}

	public InstitutionContactId getId() {
		return this.id;
	}

	public void setId(InstitutionContactId id) {
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
