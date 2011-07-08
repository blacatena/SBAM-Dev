package com.scholastic.sbam.server.database.codegen;

// Generated Jul 8, 2011 2:59:51 PM by Hibernate Tools 3.2.4.GA

/**
 * AePrefId generated by hbm2java
 */
public class AePrefId implements java.io.Serializable {

	private int aeId;
	private int auId;
	private String prefCode;

	public AePrefId() {
	}

	public AePrefId(int aeId, int auId, String prefCode) {
		this.aeId = aeId;
		this.auId = auId;
		this.prefCode = prefCode;
	}

	public int getAeId() {
		return this.aeId;
	}

	public void setAeId(int aeId) {
		this.aeId = aeId;
	}

	public int getAuId() {
		return this.auId;
	}

	public void setAuId(int auId) {
		this.auId = auId;
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
		if (!(other instanceof AePrefId))
			return false;
		AePrefId castOther = (AePrefId) other;

		return (this.getAeId() == castOther.getAeId())
				&& (this.getAuId() == castOther.getAuId())
				&& ((this.getPrefCode() == castOther.getPrefCode()) || (this
						.getPrefCode() != null
						&& castOther.getPrefCode() != null && this
						.getPrefCode().equals(castOther.getPrefCode())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getAeId();
		result = 37 * result + this.getAuId();
		result = 37 * result
				+ (getPrefCode() == null ? 0 : this.getPrefCode().hashCode());
		return result;
	}

}
