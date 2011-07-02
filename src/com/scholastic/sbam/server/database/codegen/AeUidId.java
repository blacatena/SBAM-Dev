package com.scholastic.sbam.server.database.codegen;

// Generated Jul 1, 2011 1:29:56 PM by Hibernate Tools 3.2.4.GA

/**
 * AeUidId generated by hbm2java
 */
public class AeUidId implements java.io.Serializable {

	private int aeId;
	private int auId;
	private String userId;

	public AeUidId() {
	}

	public AeUidId(int aeId, int auId, String userId) {
		this.aeId = aeId;
		this.auId = auId;
		this.userId = userId;
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

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof AeUidId))
			return false;
		AeUidId castOther = (AeUidId) other;

		return (this.getAeId() == castOther.getAeId())
				&& (this.getAuId() == castOther.getAuId())
				&& ((this.getUserId() == castOther.getUserId()) || (this
						.getUserId() != null && castOther.getUserId() != null && this
						.getUserId().equals(castOther.getUserId())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getAeId();
		result = 37 * result + this.getAuId();
		result = 37 * result
				+ (getUserId() == null ? 0 : this.getUserId().hashCode());
		return result;
	}

}
