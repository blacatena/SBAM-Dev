package com.scholastic.sbam.server.database.codegen;

// Generated Jul 4, 2011 7:39:15 PM by Hibernate Tools 3.2.4.GA

/**
 * SnapshotTermId generated by hbm2java
 */
public class SnapshotTermId implements java.io.Serializable {

	private String snapshotId;
	private int agreementId;
	private int termId;
	private String productServiceCode;
	private int ucn;
	private int ucnSuffix;

	public SnapshotTermId() {
	}

	public SnapshotTermId(String snapshotId, int agreementId, int termId,
			String productServiceCode, int ucn, int ucnSuffix) {
		this.snapshotId = snapshotId;
		this.agreementId = agreementId;
		this.termId = termId;
		this.productServiceCode = productServiceCode;
		this.ucn = ucn;
		this.ucnSuffix = ucnSuffix;
	}

	public String getSnapshotId() {
		return this.snapshotId;
	}

	public void setSnapshotId(String snapshotId) {
		this.snapshotId = snapshotId;
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

	public String getProductServiceCode() {
		return this.productServiceCode;
	}

	public void setProductServiceCode(String productServiceCode) {
		this.productServiceCode = productServiceCode;
	}

	public int getUcn() {
		return this.ucn;
	}

	public void setUcn(int ucn) {
		this.ucn = ucn;
	}

	public int getUcnSuffix() {
		return this.ucnSuffix;
	}

	public void setUcnSuffix(int ucnSuffix) {
		this.ucnSuffix = ucnSuffix;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof SnapshotTermId))
			return false;
		SnapshotTermId castOther = (SnapshotTermId) other;

		return ((this.getSnapshotId() == castOther.getSnapshotId()) || (this
				.getSnapshotId() != null && castOther.getSnapshotId() != null && this
				.getSnapshotId().equals(castOther.getSnapshotId())))
				&& (this.getAgreementId() == castOther.getAgreementId())
				&& (this.getTermId() == castOther.getTermId())
				&& ((this.getProductServiceCode() == castOther
						.getProductServiceCode()) || (this
						.getProductServiceCode() != null
						&& castOther.getProductServiceCode() != null && this
						.getProductServiceCode().equals(
								castOther.getProductServiceCode())))
				&& (this.getUcn() == castOther.getUcn())
				&& (this.getUcnSuffix() == castOther.getUcnSuffix());
	}

	public int hashCode() {
		int result = 17;

		result = 37
				* result
				+ (getSnapshotId() == null ? 0 : this.getSnapshotId()
						.hashCode());
		result = 37 * result + this.getAgreementId();
		result = 37 * result + this.getTermId();
		result = 37
				* result
				+ (getProductServiceCode() == null ? 0 : this
						.getProductServiceCode().hashCode());
		result = 37 * result + this.getUcn();
		result = 37 * result + this.getUcnSuffix();
		return result;
	}

}
