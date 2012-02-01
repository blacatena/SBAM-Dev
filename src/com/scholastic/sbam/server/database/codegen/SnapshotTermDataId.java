package com.scholastic.sbam.server.database.codegen;

// Generated Feb 1, 2012 5:33:22 PM by Hibernate Tools 3.2.4.GA

/**
 * SnapshotTermDataId generated by hbm2java
 */
public class SnapshotTermDataId implements java.io.Serializable {

	private int snapshotId;
	private int agreementId;
	private int termId;
	private String productCode;
	private String serviceCode;
	private int ucn;
	private int ucnSuffix;
	private int rowId;

	public SnapshotTermDataId() {
	}

	public SnapshotTermDataId(int snapshotId, int agreementId, int termId,
			String productCode, String serviceCode, int ucn, int ucnSuffix,
			int rowId) {
		this.snapshotId = snapshotId;
		this.agreementId = agreementId;
		this.termId = termId;
		this.productCode = productCode;
		this.serviceCode = serviceCode;
		this.ucn = ucn;
		this.ucnSuffix = ucnSuffix;
		this.rowId = rowId;
	}

	public int getSnapshotId() {
		return this.snapshotId;
	}

	public void setSnapshotId(int snapshotId) {
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

	public String getProductCode() {
		return this.productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getServiceCode() {
		return this.serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
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

	public int getRowId() {
		return this.rowId;
	}

	public void setRowId(int rowId) {
		this.rowId = rowId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof SnapshotTermDataId))
			return false;
		SnapshotTermDataId castOther = (SnapshotTermDataId) other;

		return (this.getSnapshotId() == castOther.getSnapshotId())
				&& (this.getAgreementId() == castOther.getAgreementId())
				&& (this.getTermId() == castOther.getTermId())
				&& ((this.getProductCode() == castOther.getProductCode()) || (this
						.getProductCode() != null
						&& castOther.getProductCode() != null && this
						.getProductCode().equals(castOther.getProductCode())))
				&& ((this.getServiceCode() == castOther.getServiceCode()) || (this
						.getServiceCode() != null
						&& castOther.getServiceCode() != null && this
						.getServiceCode().equals(castOther.getServiceCode())))
				&& (this.getUcn() == castOther.getUcn())
				&& (this.getUcnSuffix() == castOther.getUcnSuffix())
				&& (this.getRowId() == castOther.getRowId());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getSnapshotId();
		result = 37 * result + this.getAgreementId();
		result = 37 * result + this.getTermId();
		result = 37
				* result
				+ (getProductCode() == null ? 0 : this.getProductCode()
						.hashCode());
		result = 37
				* result
				+ (getServiceCode() == null ? 0 : this.getServiceCode()
						.hashCode());
		result = 37 * result + this.getUcn();
		result = 37 * result + this.getUcnSuffix();
		result = 37 * result + this.getRowId();
		return result;
	}

}
