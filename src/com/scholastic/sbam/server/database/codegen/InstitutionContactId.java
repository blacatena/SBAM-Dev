package com.scholastic.sbam.server.database.codegen;

// Generated Jul 8, 2011 2:59:51 PM by Hibernate Tools 3.2.4.GA

/**
 * InstitutionContactId generated by hbm2java
 */
public class InstitutionContactId implements java.io.Serializable {

	private int ucn;
	private int contactId;

	public InstitutionContactId() {
	}

	public InstitutionContactId(int ucn, int contactId) {
		this.ucn = ucn;
		this.contactId = contactId;
	}

	public int getUcn() {
		return this.ucn;
	}

	public void setUcn(int ucn) {
		this.ucn = ucn;
	}

	public int getContactId() {
		return this.contactId;
	}

	public void setContactId(int contactId) {
		this.contactId = contactId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof InstitutionContactId))
			return false;
		InstitutionContactId castOther = (InstitutionContactId) other;

		return (this.getUcn() == castOther.getUcn())
				&& (this.getContactId() == castOther.getContactId());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getUcn();
		result = 37 * result + this.getContactId();
		return result;
	}

}
