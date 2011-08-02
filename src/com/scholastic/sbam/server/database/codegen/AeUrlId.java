package com.scholastic.sbam.server.database.codegen;

// Generated Aug 1, 2011 6:30:58 PM by Hibernate Tools 3.2.4.GA

/**
 * AeUrlId generated by hbm2java
 */
public class AeUrlId implements java.io.Serializable {

	private int aeId;
	private int auId;
	private String url;

	public AeUrlId() {
	}

	public AeUrlId(int aeId, int auId, String url) {
		this.aeId = aeId;
		this.auId = auId;
		this.url = url;
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

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof AeUrlId))
			return false;
		AeUrlId castOther = (AeUrlId) other;

		return (this.getAeId() == castOther.getAeId())
				&& (this.getAuId() == castOther.getAuId())
				&& ((this.getUrl() == castOther.getUrl()) || (this.getUrl() != null
						&& castOther.getUrl() != null && this.getUrl().equals(
						castOther.getUrl())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getAeId();
		result = 37 * result + this.getAuId();
		result = 37 * result
				+ (getUrl() == null ? 0 : this.getUrl().hashCode());
		return result;
	}

}
