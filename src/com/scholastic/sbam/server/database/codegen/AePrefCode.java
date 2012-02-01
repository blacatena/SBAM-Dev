package com.scholastic.sbam.server.database.codegen;

// Generated Feb 1, 2012 5:33:22 PM by Hibernate Tools 3.2.4.GA

/**
 * AePrefCode generated by hbm2java
 */
public class AePrefCode implements java.io.Serializable {

	private AePrefCodeId id;
	private String description;
	private String defaultValue;

	public AePrefCode() {
	}

	public AePrefCode(AePrefCodeId id, String description, String defaultValue) {
		this.id = id;
		this.description = description;
		this.defaultValue = defaultValue;
	}

	public AePrefCodeId getId() {
		return this.id;
	}

	public void setId(AePrefCodeId id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

}
