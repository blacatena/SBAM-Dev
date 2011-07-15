package com.scholastic.sbam.server.database.codegen;

// Generated Jul 14, 2011 10:05:06 PM by Hibernate Tools 3.2.4.GA

/**
 * AeCst generated by hbm2java
 */
public class AeCst implements java.io.Serializable {

	private AeCstId id;
	private String institutionName;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String zip;
	private String adminUid;
	private String adminPassword;
	private String statsGroup;

	public AeCst() {
	}

	public AeCst(AeCstId id, String institutionName, String address1,
			String address2, String city, String state, String zip,
			String adminUid, String adminPassword, String statsGroup) {
		this.id = id;
		this.institutionName = institutionName;
		this.address1 = address1;
		this.address2 = address2;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.adminUid = adminUid;
		this.adminPassword = adminPassword;
		this.statsGroup = statsGroup;
	}

	public AeCstId getId() {
		return this.id;
	}

	public void setId(AeCstId id) {
		this.id = id;
	}

	public String getInstitutionName() {
		return this.institutionName;
	}

	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}

	public String getAddress1() {
		return this.address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return this.address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return this.zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getAdminUid() {
		return this.adminUid;
	}

	public void setAdminUid(String adminUid) {
		this.adminUid = adminUid;
	}

	public String getAdminPassword() {
		return this.adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	public String getStatsGroup() {
		return this.statsGroup;
	}

	public void setStatsGroup(String statsGroup) {
		this.statsGroup = statsGroup;
	}

}
