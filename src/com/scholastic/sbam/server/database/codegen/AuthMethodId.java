package com.scholastic.sbam.server.database.codegen;

// Generated Jan 31, 2011 3:49:15 PM by Hibernate Tools 3.2.4.GA

/**
 * AuthMethodId generated by hbm2java
 */
public class AuthMethodId implements java.io.Serializable {

	private int agreementId;
	private int ucn;
	private String siteLocCode;
	private String methodType;
	private String url;
	private String userId;
	private int ipFrom;

	public AuthMethodId() {
	}

	public AuthMethodId(int agreementId, int ucn, String siteLocCode,
			String methodType, String url, String userId, int ipFrom) {
		this.agreementId = agreementId;
		this.ucn = ucn;
		this.siteLocCode = siteLocCode;
		this.methodType = methodType;
		this.url = url;
		this.userId = userId;
		this.ipFrom = ipFrom;
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

	public String getSiteLocCode() {
		return this.siteLocCode;
	}

	public void setSiteLocCode(String siteLocCode) {
		this.siteLocCode = siteLocCode;
	}

	public String getMethodType() {
		return this.methodType;
	}

	public void setMethodType(String methodType) {
		this.methodType = methodType;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getIpFrom() {
		return this.ipFrom;
	}

	public void setIpFrom(int ipFrom) {
		this.ipFrom = ipFrom;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof AuthMethodId))
			return false;
		AuthMethodId castOther = (AuthMethodId) other;

		return (this.getAgreementId() == castOther.getAgreementId())
				&& (this.getUcn() == castOther.getUcn())
				&& ((this.getSiteLocCode() == castOther.getSiteLocCode()) || (this
						.getSiteLocCode() != null
						&& castOther.getSiteLocCode() != null && this
						.getSiteLocCode().equals(castOther.getSiteLocCode())))
				&& ((this.getMethodType() == castOther.getMethodType()) || (this
						.getMethodType() != null
						&& castOther.getMethodType() != null && this
						.getMethodType().equals(castOther.getMethodType())))
				&& ((this.getUrl() == castOther.getUrl()) || (this.getUrl() != null
						&& castOther.getUrl() != null && this.getUrl().equals(
						castOther.getUrl())))
				&& ((this.getUserId() == castOther.getUserId()) || (this
						.getUserId() != null && castOther.getUserId() != null && this
						.getUserId().equals(castOther.getUserId())))
				&& (this.getIpFrom() == castOther.getIpFrom());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getAgreementId();
		result = 37 * result + this.getUcn();
		result = 37
				* result
				+ (getSiteLocCode() == null ? 0 : this.getSiteLocCode()
						.hashCode());
		result = 37
				* result
				+ (getMethodType() == null ? 0 : this.getMethodType()
						.hashCode());
		result = 37 * result
				+ (getUrl() == null ? 0 : this.getUrl().hashCode());
		result = 37 * result
				+ (getUserId() == null ? 0 : this.getUserId().hashCode());
		result = 37 * result + this.getIpFrom();
		return result;
	}

}
