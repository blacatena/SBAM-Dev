package com.scholastic.sbam.server.database.codegen;

// Generated Jul 28, 2011 1:20:31 PM by Hibernate Tools 3.2.4.GA

/**
 * InstitutionGroup generated by hbm2java
 */
public class InstitutionGroup implements java.io.Serializable {

	private String groupCode;
	private String description;

	public InstitutionGroup() {
	}

	public InstitutionGroup(String groupCode, String description) {
		this.groupCode = groupCode;
		this.description = description;
	}

	public String getGroupCode() {
		return this.groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
