package com.scholastic.sbam.server.database.codegen;

// Generated Feb 1, 2012 5:33:22 PM by Hibernate Tools 3.2.4.GA

/**
 * AeConflictId generated by hbm2java
 */
public class AeConflictId implements java.io.Serializable {

	private int aeId;
	private int conflictId;

	public AeConflictId() {
	}

	public AeConflictId(int aeId, int conflictId) {
		this.aeId = aeId;
		this.conflictId = conflictId;
	}

	public int getAeId() {
		return this.aeId;
	}

	public void setAeId(int aeId) {
		this.aeId = aeId;
	}

	public int getConflictId() {
		return this.conflictId;
	}

	public void setConflictId(int conflictId) {
		this.conflictId = conflictId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof AeConflictId))
			return false;
		AeConflictId castOther = (AeConflictId) other;

		return (this.getAeId() == castOther.getAeId())
				&& (this.getConflictId() == castOther.getConflictId());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getAeId();
		result = 37 * result + this.getConflictId();
		return result;
	}

}
