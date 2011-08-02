package com.scholastic.sbam.server.database.codegen;

// Generated Aug 1, 2011 6:30:58 PM by Hibernate Tools 3.2.4.GA

/**
 * AgreementTermId generated by hbm2java
 */
public class AgreementTermId implements java.io.Serializable {

	private int agreementId;
	private int termId;

	public AgreementTermId() {
	}

	public AgreementTermId(int agreementId, int termId) {
		this.agreementId = agreementId;
		this.termId = termId;
	}

	public int getAgreementId() {
		return this.agreementId;
	}

	public void setAgreementId(int agreementId) {
		this.agreementId = agreementId;
	}

	public int getTermId() {
		return this.termId;
	}

	public void setTermId(int termId) {
		this.termId = termId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof AgreementTermId))
			return false;
		AgreementTermId castOther = (AgreementTermId) other;

		return (this.getAgreementId() == castOther.getAgreementId())
				&& (this.getTermId() == castOther.getTermId());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getAgreementId();
		result = 37 * result + this.getTermId();
		return result;
	}

}
