package com.scholastic.sbam.server.database.codegen;

// Generated Feb 1, 2012 5:33:22 PM by Hibernate Tools 3.2.4.GA

/**
 * ProductServiceId generated by hbm2java
 */
public class ProductServiceId implements java.io.Serializable {

	private String productCode;
	private String serviceCode;

	public ProductServiceId() {
	}

	public ProductServiceId(String productCode, String serviceCode) {
		this.productCode = productCode;
		this.serviceCode = serviceCode;
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

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof ProductServiceId))
			return false;
		ProductServiceId castOther = (ProductServiceId) other;

		return ((this.getProductCode() == castOther.getProductCode()) || (this
				.getProductCode() != null && castOther.getProductCode() != null && this
				.getProductCode().equals(castOther.getProductCode())))
				&& ((this.getServiceCode() == castOther.getServiceCode()) || (this
						.getServiceCode() != null
						&& castOther.getServiceCode() != null && this
						.getServiceCode().equals(castOther.getServiceCode())));
	}

	public int hashCode() {
		int result = 17;

		result = 37
				* result
				+ (getProductCode() == null ? 0 : this.getProductCode()
						.hashCode());
		result = 37
				* result
				+ (getServiceCode() == null ? 0 : this.getServiceCode()
						.hashCode());
		return result;
	}

}
