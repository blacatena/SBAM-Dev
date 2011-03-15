package com.scholastic.sbam.server.database.codegen;

// Generated Mar 3, 2011 1:32:40 PM by Hibernate Tools 3.2.4.GA

/**
 * InstitutionTypeId generated by hbm2java
 */
public class InstitutionTypeId implements java.io.Serializable {

	private String typeCode;
	private String description;
	private String longDescription;
	private String groupCode;

	public InstitutionTypeId() {
	}

	public InstitutionTypeId(String typeCode, String description,
			String longDescription, String groupCode) {
		this.typeCode = typeCode;
		this.description = description;
		this.longDescription = longDescription;
		this.groupCode = groupCode;
	}

	public String getTypeCode() {
		return this.typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLongDescription() {
		return this.longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public String getGroupCode() {
		return this.groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof InstitutionTypeId))
			return false;
		InstitutionTypeId castOther = (InstitutionTypeId) other;

		return ((this.getTypeCode() == castOther.getTypeCode()) || (this
				.getTypeCode() != null && castOther.getTypeCode() != null && this
				.getTypeCode().equals(castOther.getTypeCode())))
				&& ((this.getDescription() == castOther.getDescription()) || (this
						.getDescription() != null
						&& castOther.getDescription() != null && this
						.getDescription().equals(castOther.getDescription())))
				&& ((this.getLongDescription() == castOther
						.getLongDescription()) || (this.getLongDescription() != null
						&& castOther.getLongDescription() != null && this
						.getLongDescription().equals(
								castOther.getLongDescription())))
				&& ((this.getGroupCode() == castOther.getGroupCode()) || (this
						.getGroupCode() != null
						&& castOther.getGroupCode() != null && this
						.getGroupCode().equals(castOther.getGroupCode())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getTypeCode() == null ? 0 : this.getTypeCode().hashCode());
		result = 37
				* result
				+ (getDescription() == null ? 0 : this.getDescription()
						.hashCode());
		result = 37
				* result
				+ (getLongDescription() == null ? 0 : this.getLongDescription()
						.hashCode());
		result = 37 * result
				+ (getGroupCode() == null ? 0 : this.getGroupCode().hashCode());
		return result;
	}

}