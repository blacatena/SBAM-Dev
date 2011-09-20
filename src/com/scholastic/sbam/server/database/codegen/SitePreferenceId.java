package com.scholastic.sbam.server.database.codegen;

// Generated Sep 20, 2011 2:35:37 PM by Hibernate Tools 3.2.4.GA

/**
 * SitePreferenceId generated by hbm2java
 */
public class SitePreferenceId implements java.io.Serializable {

	private int ucn;
	private int ucnSuffix;
	private String siteLocCode;
	private String prefCatCode;

	public SitePreferenceId() {
	}

	public SitePreferenceId(int ucn, int ucnSuffix, String siteLocCode,
			String prefCatCode) {
		this.ucn = ucn;
		this.ucnSuffix = ucnSuffix;
		this.siteLocCode = siteLocCode;
		this.prefCatCode = prefCatCode;
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

	public String getPrefCatCode() {
		return this.prefCatCode;
	}

	public void setPrefCatCode(String prefCatCode) {
		this.prefCatCode = prefCatCode;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof SitePreferenceId))
			return false;
		SitePreferenceId castOther = (SitePreferenceId) other;

		return (this.getUcn() == castOther.getUcn())
				&& (this.getUcnSuffix() == castOther.getUcnSuffix())
				&& ((this.getSiteLocCode() == castOther.getSiteLocCode()) || (this
						.getSiteLocCode() != null
						&& castOther.getSiteLocCode() != null && this
						.getSiteLocCode().equals(castOther.getSiteLocCode())))
				&& ((this.getPrefCatCode() == castOther.getPrefCatCode()) || (this
						.getPrefCatCode() != null
						&& castOther.getPrefCatCode() != null && this
						.getPrefCatCode().equals(castOther.getPrefCatCode())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getUcn();
		result = 37 * result + this.getUcnSuffix();
		result = 37
				* result
				+ (getSiteLocCode() == null ? 0 : this.getSiteLocCode()
						.hashCode());
		result = 37
				* result
				+ (getPrefCatCode() == null ? 0 : this.getPrefCatCode()
						.hashCode());
		return result;
	}

}
