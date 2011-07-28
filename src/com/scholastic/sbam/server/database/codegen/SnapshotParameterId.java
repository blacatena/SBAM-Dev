package com.scholastic.sbam.server.database.codegen;

// Generated Jul 28, 2011 1:20:31 PM by Hibernate Tools 3.2.4.GA

/**
 * SnapshotParameterId generated by hbm2java
 */
public class SnapshotParameterId implements java.io.Serializable {

	private int snapshotId;
	private String parameterName;
	private int valueId;

	public SnapshotParameterId() {
	}

	public SnapshotParameterId(int snapshotId, String parameterName, int valueId) {
		this.snapshotId = snapshotId;
		this.parameterName = parameterName;
		this.valueId = valueId;
	}

	public int getSnapshotId() {
		return this.snapshotId;
	}

	public void setSnapshotId(int snapshotId) {
		this.snapshotId = snapshotId;
	}

	public String getParameterName() {
		return this.parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public int getValueId() {
		return this.valueId;
	}

	public void setValueId(int valueId) {
		this.valueId = valueId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof SnapshotParameterId))
			return false;
		SnapshotParameterId castOther = (SnapshotParameterId) other;

		return (this.getSnapshotId() == castOther.getSnapshotId())
				&& ((this.getParameterName() == castOther.getParameterName()) || (this
						.getParameterName() != null
						&& castOther.getParameterName() != null && this
						.getParameterName()
						.equals(castOther.getParameterName())))
				&& (this.getValueId() == castOther.getValueId());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getSnapshotId();
		result = 37
				* result
				+ (getParameterName() == null ? 0 : this.getParameterName()
						.hashCode());
		result = 37 * result + this.getValueId();
		return result;
	}

}
