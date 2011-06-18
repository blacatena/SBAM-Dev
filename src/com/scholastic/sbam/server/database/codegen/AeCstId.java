package com.scholastic.sbam.server.database.codegen;

// Generated Jun 16, 2011 5:48:55 PM by Hibernate Tools 3.2.4.GA

/**
 * AeCstId generated by hbm2java
 */
public class AeCstId implements java.io.Serializable {

	private int aeId;
	private String customerCode;

	public AeCstId() {
	}

	public AeCstId(int aeId, String customerCode) {
		this.aeId = aeId;
		this.customerCode = customerCode;
	}

	public int getAeId() {
		return this.aeId;
	}

	public void setAeId(int aeId) {
		this.aeId = aeId;
	}

	public String getCustomerCode() {
		return this.customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof AeCstId))
			return false;
		AeCstId castOther = (AeCstId) other;

		return (this.getAeId() == castOther.getAeId())
				&& ((this.getCustomerCode() == castOther.getCustomerCode()) || (this
						.getCustomerCode() != null
						&& castOther.getCustomerCode() != null && this
						.getCustomerCode().equals(castOther.getCustomerCode())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getAeId();
		result = 37
				* result
				+ (getCustomerCode() == null ? 0 : this.getCustomerCode()
						.hashCode());
		return result;
	}

}