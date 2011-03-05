package com.scholastic.sbam.shared.objects;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class InstitutionInstance implements BeanModelTag, IsSerializable {
	private	int 	ucn;
	private	String	institutionName;
	private	String	address1;
	private	String	address2;
	private	String	address3;
	private	String	city;
	private	String	state;
	private	String	zip;
	private	String	country;
	private	String	webUrl;
	private	String	phone;
	private	String	fax;
	private String  typeCode;
	private String  typeDescription;
	private String	groupCode;
	private String  groupDescription;
	private String	publicPrivateCode;
	private String  publicPrivateDescription;
	private String	alternateIds;
	private Date	createdDate;
	private Date	closedDate;
	
	public int getUcn() {
		return ucn;
	}
	public void setUcn(int ucn) {
		this.ucn = ucn;
	}
	public String getInstitutionName() {
		return institutionName;
	}
	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getAddress3() {
		return address3;
	}
	public void setAddress3(String address3) {
		this.address3 = address3;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getWebUrl() {
		return webUrl;
	}
	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getTypeCode() {
		return typeCode;
	}
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	public String getTypeDescription() {
		return typeDescription;
	}
	public void setTypeDescription(String typeDescription) {
		this.typeDescription = typeDescription;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public String getGroupDescription() {
		return groupDescription;
	}
	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}
	public String getAlternateIds() {
		return alternateIds;
	}
	public void setAlternateIds(String alternateIds) {
		this.alternateIds = alternateIds;
	}
	public String getPublicPrivateCode() {
		return publicPrivateCode;
	}
	public void setPublicPrivateCode(String publicPrivateCode) {
		this.publicPrivateCode = publicPrivateCode;
	}
	public String getPublicPrivateDescription() {
		return publicPrivateDescription;
	}
	public void setPublicPrivateDescription(String publicPrivateDescription) {
		this.publicPrivateDescription = publicPrivateDescription;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getClosedDate() {
		return closedDate;
	}
	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}
	
}
