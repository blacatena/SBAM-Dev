package com.scholastic.sbam.server.database.codegen;

// Generated Jun 28, 2011 11:14:17 AM by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * AlienInstitutionId generated by hbm2java
 */
public class AlienInstitutionId implements java.io.Serializable {

	private int ucn;
	private int parentUcn;
	private String institutionName;
	private String address1;
	private String address2;
	private String address3;
	private String city;
	private String state;
	private String zip;
	private String country;
	private String webUrl;
	private String phone;
	private String fax;
	private String mailAddress1;
	private String mailAddress2;
	private String mailAddress3;
	private String mailCity;
	private String mailState;
	private String mailZip;
	private String mailCountry;
	private String typeCode;
	private String groupCode;
	private String publicPrivateCode;
	private String alternateIds;
	private String source;
	private Date createdDate;
	private Date closedDate;
	private char status;

	public AlienInstitutionId() {
	}

	public AlienInstitutionId(int ucn, int parentUcn, String institutionName,
			String address1, String city, String country, String typeCode,
			String groupCode, String publicPrivateCode, String alternateIds,
			String source, char status) {
		this.ucn = ucn;
		this.parentUcn = parentUcn;
		this.institutionName = institutionName;
		this.address1 = address1;
		this.city = city;
		this.country = country;
		this.typeCode = typeCode;
		this.groupCode = groupCode;
		this.publicPrivateCode = publicPrivateCode;
		this.alternateIds = alternateIds;
		this.source = source;
		this.status = status;
	}

	public AlienInstitutionId(int ucn, int parentUcn, String institutionName,
			String address1, String address2, String address3, String city,
			String state, String zip, String country, String webUrl,
			String phone, String fax, String mailAddress1, String mailAddress2,
			String mailAddress3, String mailCity, String mailState,
			String mailZip, String mailCountry, String typeCode,
			String groupCode, String publicPrivateCode, String alternateIds,
			String source, Date createdDate, Date closedDate, char status) {
		this.ucn = ucn;
		this.parentUcn = parentUcn;
		this.institutionName = institutionName;
		this.address1 = address1;
		this.address2 = address2;
		this.address3 = address3;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.country = country;
		this.webUrl = webUrl;
		this.phone = phone;
		this.fax = fax;
		this.mailAddress1 = mailAddress1;
		this.mailAddress2 = mailAddress2;
		this.mailAddress3 = mailAddress3;
		this.mailCity = mailCity;
		this.mailState = mailState;
		this.mailZip = mailZip;
		this.mailCountry = mailCountry;
		this.typeCode = typeCode;
		this.groupCode = groupCode;
		this.publicPrivateCode = publicPrivateCode;
		this.alternateIds = alternateIds;
		this.source = source;
		this.createdDate = createdDate;
		this.closedDate = closedDate;
		this.status = status;
	}

	public int getUcn() {
		return this.ucn;
	}

	public void setUcn(int ucn) {
		this.ucn = ucn;
	}

	public int getParentUcn() {
		return this.parentUcn;
	}

	public void setParentUcn(int parentUcn) {
		this.parentUcn = parentUcn;
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

	public String getAddress3() {
		return this.address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
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

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getWebUrl() {
		return this.webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFax() {
		return this.fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getMailAddress1() {
		return this.mailAddress1;
	}

	public void setMailAddress1(String mailAddress1) {
		this.mailAddress1 = mailAddress1;
	}

	public String getMailAddress2() {
		return this.mailAddress2;
	}

	public void setMailAddress2(String mailAddress2) {
		this.mailAddress2 = mailAddress2;
	}

	public String getMailAddress3() {
		return this.mailAddress3;
	}

	public void setMailAddress3(String mailAddress3) {
		this.mailAddress3 = mailAddress3;
	}

	public String getMailCity() {
		return this.mailCity;
	}

	public void setMailCity(String mailCity) {
		this.mailCity = mailCity;
	}

	public String getMailState() {
		return this.mailState;
	}

	public void setMailState(String mailState) {
		this.mailState = mailState;
	}

	public String getMailZip() {
		return this.mailZip;
	}

	public void setMailZip(String mailZip) {
		this.mailZip = mailZip;
	}

	public String getMailCountry() {
		return this.mailCountry;
	}

	public void setMailCountry(String mailCountry) {
		this.mailCountry = mailCountry;
	}

	public String getTypeCode() {
		return this.typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getGroupCode() {
		return this.groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getPublicPrivateCode() {
		return this.publicPrivateCode;
	}

	public void setPublicPrivateCode(String publicPrivateCode) {
		this.publicPrivateCode = publicPrivateCode;
	}

	public String getAlternateIds() {
		return this.alternateIds;
	}

	public void setAlternateIds(String alternateIds) {
		this.alternateIds = alternateIds;
	}

	public String getSource() {
		return this.source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getClosedDate() {
		return this.closedDate;
	}

	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}

	public char getStatus() {
		return this.status;
	}

	public void setStatus(char status) {
		this.status = status;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof AlienInstitutionId))
			return false;
		AlienInstitutionId castOther = (AlienInstitutionId) other;

		return (this.getUcn() == castOther.getUcn())
				&& (this.getParentUcn() == castOther.getParentUcn())
				&& ((this.getInstitutionName() == castOther
						.getInstitutionName()) || (this.getInstitutionName() != null
						&& castOther.getInstitutionName() != null && this
						.getInstitutionName().equals(
								castOther.getInstitutionName())))
				&& ((this.getAddress1() == castOther.getAddress1()) || (this
						.getAddress1() != null
						&& castOther.getAddress1() != null && this
						.getAddress1().equals(castOther.getAddress1())))
				&& ((this.getAddress2() == castOther.getAddress2()) || (this
						.getAddress2() != null
						&& castOther.getAddress2() != null && this
						.getAddress2().equals(castOther.getAddress2())))
				&& ((this.getAddress3() == castOther.getAddress3()) || (this
						.getAddress3() != null
						&& castOther.getAddress3() != null && this
						.getAddress3().equals(castOther.getAddress3())))
				&& ((this.getCity() == castOther.getCity()) || (this.getCity() != null
						&& castOther.getCity() != null && this.getCity()
						.equals(castOther.getCity())))
				&& ((this.getState() == castOther.getState()) || (this
						.getState() != null && castOther.getState() != null && this
						.getState().equals(castOther.getState())))
				&& ((this.getZip() == castOther.getZip()) || (this.getZip() != null
						&& castOther.getZip() != null && this.getZip().equals(
						castOther.getZip())))
				&& ((this.getCountry() == castOther.getCountry()) || (this
						.getCountry() != null && castOther.getCountry() != null && this
						.getCountry().equals(castOther.getCountry())))
				&& ((this.getWebUrl() == castOther.getWebUrl()) || (this
						.getWebUrl() != null && castOther.getWebUrl() != null && this
						.getWebUrl().equals(castOther.getWebUrl())))
				&& ((this.getPhone() == castOther.getPhone()) || (this
						.getPhone() != null && castOther.getPhone() != null && this
						.getPhone().equals(castOther.getPhone())))
				&& ((this.getFax() == castOther.getFax()) || (this.getFax() != null
						&& castOther.getFax() != null && this.getFax().equals(
						castOther.getFax())))
				&& ((this.getMailAddress1() == castOther.getMailAddress1()) || (this
						.getMailAddress1() != null
						&& castOther.getMailAddress1() != null && this
						.getMailAddress1().equals(castOther.getMailAddress1())))
				&& ((this.getMailAddress2() == castOther.getMailAddress2()) || (this
						.getMailAddress2() != null
						&& castOther.getMailAddress2() != null && this
						.getMailAddress2().equals(castOther.getMailAddress2())))
				&& ((this.getMailAddress3() == castOther.getMailAddress3()) || (this
						.getMailAddress3() != null
						&& castOther.getMailAddress3() != null && this
						.getMailAddress3().equals(castOther.getMailAddress3())))
				&& ((this.getMailCity() == castOther.getMailCity()) || (this
						.getMailCity() != null
						&& castOther.getMailCity() != null && this
						.getMailCity().equals(castOther.getMailCity())))
				&& ((this.getMailState() == castOther.getMailState()) || (this
						.getMailState() != null
						&& castOther.getMailState() != null && this
						.getMailState().equals(castOther.getMailState())))
				&& ((this.getMailZip() == castOther.getMailZip()) || (this
						.getMailZip() != null && castOther.getMailZip() != null && this
						.getMailZip().equals(castOther.getMailZip())))
				&& ((this.getMailCountry() == castOther.getMailCountry()) || (this
						.getMailCountry() != null
						&& castOther.getMailCountry() != null && this
						.getMailCountry().equals(castOther.getMailCountry())))
				&& ((this.getTypeCode() == castOther.getTypeCode()) || (this
						.getTypeCode() != null
						&& castOther.getTypeCode() != null && this
						.getTypeCode().equals(castOther.getTypeCode())))
				&& ((this.getGroupCode() == castOther.getGroupCode()) || (this
						.getGroupCode() != null
						&& castOther.getGroupCode() != null && this
						.getGroupCode().equals(castOther.getGroupCode())))
				&& ((this.getPublicPrivateCode() == castOther
						.getPublicPrivateCode()) || (this
						.getPublicPrivateCode() != null
						&& castOther.getPublicPrivateCode() != null && this
						.getPublicPrivateCode().equals(
								castOther.getPublicPrivateCode())))
				&& ((this.getAlternateIds() == castOther.getAlternateIds()) || (this
						.getAlternateIds() != null
						&& castOther.getAlternateIds() != null && this
						.getAlternateIds().equals(castOther.getAlternateIds())))
				&& ((this.getSource() == castOther.getSource()) || (this
						.getSource() != null && castOther.getSource() != null && this
						.getSource().equals(castOther.getSource())))
				&& ((this.getCreatedDate() == castOther.getCreatedDate()) || (this
						.getCreatedDate() != null
						&& castOther.getCreatedDate() != null && this
						.getCreatedDate().equals(castOther.getCreatedDate())))
				&& ((this.getClosedDate() == castOther.getClosedDate()) || (this
						.getClosedDate() != null
						&& castOther.getClosedDate() != null && this
						.getClosedDate().equals(castOther.getClosedDate())))
				&& (this.getStatus() == castOther.getStatus());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getUcn();
		result = 37 * result + this.getParentUcn();
		result = 37
				* result
				+ (getInstitutionName() == null ? 0 : this.getInstitutionName()
						.hashCode());
		result = 37 * result
				+ (getAddress1() == null ? 0 : this.getAddress1().hashCode());
		result = 37 * result
				+ (getAddress2() == null ? 0 : this.getAddress2().hashCode());
		result = 37 * result
				+ (getAddress3() == null ? 0 : this.getAddress3().hashCode());
		result = 37 * result
				+ (getCity() == null ? 0 : this.getCity().hashCode());
		result = 37 * result
				+ (getState() == null ? 0 : this.getState().hashCode());
		result = 37 * result
				+ (getZip() == null ? 0 : this.getZip().hashCode());
		result = 37 * result
				+ (getCountry() == null ? 0 : this.getCountry().hashCode());
		result = 37 * result
				+ (getWebUrl() == null ? 0 : this.getWebUrl().hashCode());
		result = 37 * result
				+ (getPhone() == null ? 0 : this.getPhone().hashCode());
		result = 37 * result
				+ (getFax() == null ? 0 : this.getFax().hashCode());
		result = 37
				* result
				+ (getMailAddress1() == null ? 0 : this.getMailAddress1()
						.hashCode());
		result = 37
				* result
				+ (getMailAddress2() == null ? 0 : this.getMailAddress2()
						.hashCode());
		result = 37
				* result
				+ (getMailAddress3() == null ? 0 : this.getMailAddress3()
						.hashCode());
		result = 37 * result
				+ (getMailCity() == null ? 0 : this.getMailCity().hashCode());
		result = 37 * result
				+ (getMailState() == null ? 0 : this.getMailState().hashCode());
		result = 37 * result
				+ (getMailZip() == null ? 0 : this.getMailZip().hashCode());
		result = 37
				* result
				+ (getMailCountry() == null ? 0 : this.getMailCountry()
						.hashCode());
		result = 37 * result
				+ (getTypeCode() == null ? 0 : this.getTypeCode().hashCode());
		result = 37 * result
				+ (getGroupCode() == null ? 0 : this.getGroupCode().hashCode());
		result = 37
				* result
				+ (getPublicPrivateCode() == null ? 0 : this
						.getPublicPrivateCode().hashCode());
		result = 37
				* result
				+ (getAlternateIds() == null ? 0 : this.getAlternateIds()
						.hashCode());
		result = 37 * result
				+ (getSource() == null ? 0 : this.getSource().hashCode());
		result = 37
				* result
				+ (getCreatedDate() == null ? 0 : this.getCreatedDate()
						.hashCode());
		result = 37
				* result
				+ (getClosedDate() == null ? 0 : this.getClosedDate()
						.hashCode());
		result = 37 * result + this.getStatus();
		return result;
	}

}
