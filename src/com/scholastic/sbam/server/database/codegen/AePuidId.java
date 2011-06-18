package com.scholastic.sbam.server.database.codegen;

// Generated Jun 16, 2011 5:48:55 PM by Hibernate Tools 3.2.4.GA

/**
 * AePuidId generated by hbm2java
 */
public class AePuidId implements java.io.Serializable {

	private int aeId;
	private int auId;
	private String userId;
	private String ip;

	public AePuidId() {
	}

	public AePuidId(int aeId, int auId, String userId, String ip) {
		this.aeId = aeId;
		this.auId = auId;
		this.userId = userId;
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

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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
		if (!(other instanceof AePuidId))
			return false;
		AePuidId castOther = (AePuidId) other;

		return (this.getAeId() == castOther.getAeId())
				&& (this.getAuId() == castOther.getAuId())
				&& ((this.getUserId() == castOther.getUserId()) || (this
						.getUserId() != null && castOther.getUserId() != null && this
						.getUserId().equals(castOther.getUserId())))
				&& ((this.getIp() == castOther.getIp()) || (this.getIp() != null
						&& castOther.getIp() != null && this.getIp().equals(
						castOther.getIp())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getAeId();
		result = 37 * result + this.getAuId();
		result = 37 * result
				+ (getUserId() == null ? 0 : this.getUserId().hashCode());
		result = 37 * result + (getIp() == null ? 0 : this.getIp().hashCode());
		return result;
	}

}