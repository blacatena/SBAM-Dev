package com.scholastic.sbam.server.database.codegen;

// Generated Jul 28, 2011 1:20:31 PM by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * AgreementContact generated by hbm2java
 */
public class AgreementContact implements java.io.Serializable {

	private AgreementContactId id;
	private char renewalContact;
	private Date createdDatetime;
	private char status;

	public AgreementContact() {
	}

	public AgreementContact(AgreementContactId id, char renewalContact,
			Date createdDatetime, char status) {
		this.id = id;
		this.renewalContact = renewalContact;
		this.createdDatetime = createdDatetime;
		this.status = status;
	}

	public AgreementContactId getId() {
		return this.id;
	}

	public void setId(AgreementContactId id) {
		this.id = id;
	}

	public char getRenewalContact() {
		return this.renewalContact;
	}

	public void setRenewalContact(char renewalContact) {
		this.renewalContact = renewalContact;
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
