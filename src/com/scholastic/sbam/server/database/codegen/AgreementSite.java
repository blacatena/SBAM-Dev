package com.scholastic.sbam.server.database.codegen;

// Generated Jan 19, 2011 12:27:01 PM by Hibernate Tools 3.2.4.GA

/**
 * AgreementSite generated by hbm2java
 */
public class AgreementSite implements java.io.Serializable {

	private AgreementSiteId id;
	private int siteUcn;

	public AgreementSite() {
	}

	public AgreementSite(AgreementSiteId id, int siteUcn) {
		this.id = id;
		this.siteUcn = siteUcn;
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

}
