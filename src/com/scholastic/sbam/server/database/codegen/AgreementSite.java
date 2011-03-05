package com.scholastic.sbam.server.database.codegen;

// Generated Mar 4, 2011 3:03:09 PM by Hibernate Tools 3.2.4.GA

/**
 * AgreementSite generated by hbm2java
 */
public class AgreementSite implements java.io.Serializable {

	private AgreementSiteId id;
	private int siteUcn;
	private int siteUcnSuffix;
	private String commissionCode;

	public AgreementSite() {
	}

	public AgreementSite(AgreementSiteId id, int siteUcn, int siteUcnSuffix,
			String commissionCode) {
		this.id = id;
		this.siteUcn = siteUcn;
		this.siteUcnSuffix = siteUcnSuffix;
		this.commissionCode = commissionCode;
	}

	public AgreementSiteId getId() {
		return this.id;
	}

	public void setId(AgreementSiteId id) {
		this.id = id;
	}

	public int getSiteUcn() {
		return this.siteUcn;
	}

	public void setSiteUcn(int siteUcn) {
		this.siteUcn = siteUcn;
	}

	public int getSiteUcnSuffix() {
		return this.siteUcnSuffix;
	}

	public void setSiteUcnSuffix(int siteUcnSuffix) {
		this.siteUcnSuffix = siteUcnSuffix;
	}

	public String getCommissionCode() {
		return this.commissionCode;
	}

	public void setCommissionCode(String commissionCode) {
		this.commissionCode = commissionCode;
	}

}
