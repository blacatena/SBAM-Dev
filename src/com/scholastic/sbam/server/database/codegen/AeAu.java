package com.scholastic.sbam.server.database.codegen;

// Generated Sep 20, 2011 2:35:37 PM by Hibernate Tools 3.2.4.GA

/**
 * AeAu generated by hbm2java
 */
public class AeAu implements java.io.Serializable {

	private AeAuId id;
	private String siteParentCode;
	private String billCode;
	private String siteCode;
	private String siteLocCode;

	public AeAu() {
	}

	public AeAu(AeAuId id, String siteParentCode, String billCode,
			String siteCode, String siteLocCode) {
		this.id = id;
		this.siteParentCode = siteParentCode;
		this.billCode = billCode;
		this.siteCode = siteCode;
		this.siteLocCode = siteLocCode;
	}

	public AeAuId getId() {
		return this.id;
	}

	public void setId(AeAuId id) {
		this.id = id;
	}

	public String getSiteParentCode() {
		return this.siteParentCode;
	}

	public void setSiteParentCode(String siteParentCode) {
		this.siteParentCode = siteParentCode;
	}

	public String getBillCode() {
		return this.billCode;
	}

	public void setBillCode(String billCode) {
		this.billCode = billCode;
	}

	public String getSiteCode() {
		return this.siteCode;
	}

	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}

	public String getSiteLocCode() {
		return this.siteLocCode;
	}

	public void setSiteLocCode(String siteLocCode) {
		this.siteLocCode = siteLocCode;
	}

}
