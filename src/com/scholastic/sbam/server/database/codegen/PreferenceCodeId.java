package com.scholastic.sbam.server.database.codegen;

// Generated Dec 22, 2010 4:10:32 PM by Hibernate Tools 3.2.4.GA

/**
 * PreferenceCodeId generated by hbm2java
 */
public class PreferenceCodeId implements java.io.Serializable {

	private String prefCatCode;
	private String prefSelCode;

	public PreferenceCodeId() {
	}

	public PreferenceCodeId(String prefCatCode, String prefSelCode) {
		this.prefCatCode = prefCatCode;
		this.prefSelCode = prefSelCode;
	}

	public String getPrefCatCode() {
		return this.prefCatCode;
	}

	public void setPrefCatCode(String prefCatCode) {
		this.prefCatCode = prefCatCode;
	}

	public String getPrefSelCode() {
		return this.prefSelCode;
	}

	public void setPrefSelCode(String prefSelCode) {
		this.prefSelCode = prefSelCode;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof PreferenceCodeId))
			return false;
		PreferenceCodeId castOther = (PreferenceCodeId) other;

		return ((this.getPrefCatCode() == castOther.getPrefCatCode()) || (this
				.getPrefCatCode() != null && castOther.getPrefCatCode() != null && this
				.getPrefCatCode().equals(castOther.getPrefCatCode())))
				&& ((this.getPrefSelCode() == castOther.getPrefSelCode()) || (this
						.getPrefSelCode() != null
						&& castOther.getPrefSelCode() != null && this
						.getPrefSelCode().equals(castOther.getPrefSelCode())));
	}

	public int hashCode() {
		int result = 17;

		result = 37
				* result
				+ (getPrefCatCode() == null ? 0 : this.getPrefCatCode()
						.hashCode());
		result = 37
				* result
				+ (getPrefSelCode() == null ? 0 : this.getPrefSelCode()
						.hashCode());
		return result;
	}

}