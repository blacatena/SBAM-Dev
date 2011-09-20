package com.scholastic.sbam.server.database.codegen;

// Generated Sep 20, 2011 2:35:37 PM by Hibernate Tools 3.2.4.GA

/**
 * AePrefCodeId generated by hbm2java
 */
public class AePrefCodeId implements java.io.Serializable {

	private int aeId;
	private String prefCode;

	public AePrefCodeId() {
	}

	public AePrefCodeId(int aeId, String prefCode) {
		this.aeId = aeId;
		this.prefCode = prefCode;
	}

	public int getAeId() {
		return this.aeId;
	}

	public void setAeId(int aeId) {
		this.aeId = aeId;
	}

	public String getPrefCode() {
		return this.prefCode;
	}

	public void setPrefCode(String prefCode) {
		this.prefCode = prefCode;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof AePrefCodeId))
			return false;
		AePrefCodeId castOther = (AePrefCodeId) other;

		return (this.getAeId() == castOther.getAeId())
				&& ((this.getPrefCode() == castOther.getPrefCode()) || (this
						.getPrefCode() != null
						&& castOther.getPrefCode() != null && this
						.getPrefCode().equals(castOther.getPrefCode())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getAeId();
		result = 37 * result
				+ (getPrefCode() == null ? 0 : this.getPrefCode().hashCode());
		return result;
	}

}
