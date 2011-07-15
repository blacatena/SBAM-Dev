package com.scholastic.sbam.server.database.codegen;

// Generated Jul 14, 2011 10:05:06 PM by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * Contact generated by hbm2java
 */
public class Contact implements java.io.Serializable {

	private Integer contactId;
	private String contactTypeCode;
	private int parentUcn;
	private String fullName;
	private String title;
	private String additionalInfo;
	private String address1;
	private String address2;
	private String address3;
	private String city;
	private String state;
	private String zip;
	private String country;
	private String EMail;
	private String EMail2;
	private String phone;
	private String phone2;
	private String fax;
	private String note;
	private Date createdDatetime;
	private char status;

	public Contact() {
	}

	public Contact(String contactTypeCode, int parentUcn, String fullName,
			String title, String additionalInfo, String address1,
			String address2, String address3, String city, String state,
			String zip, String country, String EMail, String EMail2,
			String phone, String phone2, String fax, String note,
			Date createdDatetime, char status) {
		this.contactTypeCode = contactTypeCode;
		this.parentUcn = parentUcn;
		this.fullName = fullName;
		this.title = title;
		this.additionalInfo = additionalInfo;
		this.address1 = address1;
		this.address2 = address2;
		this.address3 = address3;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.country = country;
		this.EMail = EMail;
		this.EMail2 = EMail2;
		this.phone = phone;
		this.phone2 = phone2;
		this.fax = fax;
		this.note = note;
		this.createdDatetime = createdDatetime;
		this.status = status;
	}

	public Integer getContactId() {
		return this.contactId;
	}

	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}

	public String getContactTypeCode() {
		return this.contactTypeCode;
	}

	public void setContactTypeCode(String contactTypeCode) {
		this.contactTypeCode = contactTypeCode;
	}

	public int getParentUcn() {
		return this.parentUcn;
	}

	public void setParentUcn(int parentUcn) {
		this.parentUcn = parentUcn;
	}

	public String getFullName() {
		return this.fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAdditionalInfo() {
		return this.additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
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

	public String getEMail() {
		return this.EMail;
	}

	public void setEMail(String EMail) {
		this.EMail = EMail;
	}

	public String getEMail2() {
		return this.EMail2;
	}

	public void setEMail2(String EMail2) {
		this.EMail2 = EMail2;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhone2() {
		return this.phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public String getFax() {
		return this.fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Date getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Date createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public char getStatus() {
		return this.status;
	}

	public void setStatus(char status) {
		this.status = status;
	}

}
