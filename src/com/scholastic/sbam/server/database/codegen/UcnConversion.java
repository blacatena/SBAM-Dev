package com.scholastic.sbam.server.database.codegen;

// Generated Jul 18, 2011 4:22:00 PM by Hibernate Tools 3.2.4.GA

/**
 * UcnConversion generated by hbm2java
 */
public class UcnConversion implements java.io.Serializable {

	private UcnConversionId id;
	private String oldCustomerCode;

	public UcnConversion() {
	}

	public UcnConversion(UcnConversionId id, String oldCustomerCode) {
		this.id = id;
		this.oldCustomerCode = oldCustomerCode;
	}

	public UcnConversionId getId() {
		return this.id;
	}

	public void setId(UcnConversionId id) {
		this.id = id;
	}

	public String getOldCustomerCode() {
		return this.oldCustomerCode;
	}

	public void setOldCustomerCode(String oldCustomerCode) {
		this.oldCustomerCode = oldCustomerCode;
	}

}
