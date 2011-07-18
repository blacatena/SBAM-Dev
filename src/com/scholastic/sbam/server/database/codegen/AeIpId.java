package com.scholastic.sbam.server.database.codegen;

// Generated Jul 18, 2011 4:22:00 PM by Hibernate Tools 3.2.4.GA

/**
 * AeIpId generated by hbm2java
 */
public class AeIpId implements java.io.Serializable {

	private int aeId;
	private int auId;
	private String ip;

	public AeIpId() {
	}

	public AeIpId(int aeId, int auId, String ip) {
		this.aeId = aeId;
		this.auId = auId;
		this.ip = ip;
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

	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof AeIpId))
			return false;
		AeIpId castOther = (AeIpId) other;

		return (this.getAeId() == castOther.getAeId())
				&& (this.getAuId() == castOther.getAuId())
				&& ((this.getIp() == castOther.getIp()) || (this.getIp() != null
						&& castOther.getIp() != null && this.getIp().equals(
						castOther.getIp())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getAeId();
		result = 37 * result + this.getAuId();
		result = 37 * result + (getIp() == null ? 0 : this.getIp().hashCode());
		return result;
	}

}
