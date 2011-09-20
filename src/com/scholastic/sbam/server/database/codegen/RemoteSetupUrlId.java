package com.scholastic.sbam.server.database.codegen;

// Generated Sep 20, 2011 2:35:37 PM by Hibernate Tools 3.2.4.GA

/**
 * RemoteSetupUrlId generated by hbm2java
 */
public class RemoteSetupUrlId implements java.io.Serializable {

	private int agreementId;
	private int ucn;
	private int ucnSuffix;
	private String siteLocCode;
	private int urlId;

	public RemoteSetupUrlId() {
	}

	public RemoteSetupUrlId(int agreementId, int ucn, int ucnSuffix,
			String siteLocCode, int urlId) {
		this.agreementId = agreementId;
		this.ucn = ucn;
		this.ucnSuffix = ucnSuffix;
		this.siteLocCode = siteLocCode;
		this.urlId = urlId;
	}

	public int getAgreementId() {
		return this.agreementId;
	}

	public void setAgreementId(int agreementId) {
		this.agreementId = agreementId;
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

	public String getSiteLocCode() {
		return this.siteLocCode;
	}

	public void setSiteLocCode(String siteLocCode) {
		this.siteLocCode = siteLocCode;
	}

	public int getUrlId() {
		return this.urlId;
	}

	public void setUrlId(int urlId) {
		this.urlId = urlId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof RemoteSetupUrlId))
			return false;
		RemoteSetupUrlId castOther = (RemoteSetupUrlId) other;

		return (this.getAgreementId() == castOther.getAgreementId())
				&& (this.getUcn() == castOther.getUcn())
				&& (this.getUcnSuffix() == castOther.getUcnSuffix())
				&& ((this.getSiteLocCode() == castOther.getSiteLocCode()) || (this
						.getSiteLocCode() != null
						&& castOther.getSiteLocCode() != null && this
						.getSiteLocCode().equals(castOther.getSiteLocCode())))
				&& (this.getUrlId() == castOther.getUrlId());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getAgreementId();
		result = 37 * result + this.getUcn();
		result = 37 * result + this.getUcnSuffix();
		result = 37
				* result
				+ (getSiteLocCode() == null ? 0 : this.getSiteLocCode()
						.hashCode());
		result = 37 * result + this.getUrlId();
		return result;
	}

}
