package com.scholastic.sbam.shared.objects;

import java.util.Date;
import java.util.SortedMap;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.scholastic.sbam.client.util.AddressFormatter;
import com.scholastic.sbam.shared.util.AppConstants;

public class InstitutionInstance implements BeanModelTag, IsSerializable, UserCacheTarget {

	private static BeanModelFactory beanModelfactory;
	
	private	int 	ucn;
	private int		parentUcn;
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
	private char 	status;
	
	private int		agreements;
	private int		activeAgreements;
	private Date	lastServiceDate;
	private SortedMap<Integer, AgreementSummaryInstance> agreementSummaryList;
	
	public int getUcn() {
		return ucn;
	}
	public void setUcn(int ucn) {
		this.ucn = ucn;
	}
	public int getParentUcn() {
		return parentUcn;
	}
	public void setParentUcn(int parentUcn) {
		this.parentUcn = parentUcn;
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
		
	public char getStatus() {
		return status;
	}
	public void setStatus(char status) {
		this.status = status;
	}
	public int getAgreements() {
		return agreements;
	}
	public void setAgreements(int agreements) {
		this.agreements = agreements;
	}
	public int getActiveAgreements() {
		return activeAgreements;
	}
	public void setActiveAgreements(int activeAgreements) {
		this.activeAgreements = activeAgreements;
	}
	public Date getLastServiceDate() {
		return lastServiceDate;
	}
	public void setLastServiceDate(Date lastServiceDate) {
		this.lastServiceDate = lastServiceDate;
	}
	public String getNameAndUcn() {
		if (ucn <= 0)
			return institutionName;
		return institutionName + " [ " + ucn + " ]";
	}
	public String getHtmlAddress() {
		return AddressFormatter.getMultiLineAddress(address1, address2, address3, city, state, zip, country);
	}
	public String getAgreementCountCombo() {
		if (agreements == 0)
			return "";
		return activeAgreements + " ( " + agreements + " )";
	}
	
	public String getListStyle() {
		if (status == AppConstants.STATUS_NEW)
			return "list-new";
		if (status == AppConstants.STATUS_ALL)
			return "list-all";
		if (status == AppConstants.STATUS_ERROR)
			return "list-alert";
		return "list-normal";
	}
	
	public SortedMap<Integer, AgreementSummaryInstance> getAgreementSummaryList() {
		return agreementSummaryList;
	}
	public void setAgreementSummaryList(SortedMap<Integer, AgreementSummaryInstance> agreementSummaryList) {
		this.agreementSummaryList = agreementSummaryList;
		agreements = 0;
		activeAgreements = 0;
		lastServiceDate = null;
		Date today = new Date();
		if (this.agreementSummaryList != null) {
			for (AgreementSummaryInstance instance : agreementSummaryList.values()) {
				agreements++;
				if (instance.hasExpired(today)) {
					activeAgreements++;
				}
				if (instance.getEndDate() != null)
					if (lastServiceDate == null || instance.getEndDate().after(lastServiceDate) )
						lastServiceDate = (Date) instance.getEndDate().clone();
			}
		//	System.out.println("UCN " + ucn + " agreements " + agreements + ", active " + activeAgreements + ", last date " + lastServiceDate);
		}
	}
	
	public boolean equals(InstitutionInstance other) {
		if (other == null)
			return false;
		return (other.ucn == this.ucn);
	}
	
	public static InstitutionInstance getEmptyInstance() {
		InstitutionInstance instance = new InstitutionInstance();
		instance.ucn = 0;
		instance.institutionName = "";
		return instance;
	}
	
	public static InstitutionInstance getUnknownInstance(int ucn) {
		InstitutionInstance instance = new InstitutionInstance();
		instance.ucn = ucn;
		instance.institutionName = "Unknown institution " + ucn;
		return instance;
	}
	
	public static InstitutionInstance getErrorInstance(String message) {
		InstitutionInstance instance = new InstitutionInstance();
		instance.ucn = 0;
		instance.institutionName = message;
		instance.status = AppConstants.STATUS_ERROR;
		return instance;
	}

	public static BeanModel obtainModel(InstitutionInstance instance) {
		if (beanModelfactory == null)
			beanModelfactory  = BeanModelLookup.get().getFactory(InstitutionInstance.class);
		BeanModel model = beanModelfactory.createModel(instance);
		return model;
	}

	public static String getUserCacheCategory() {
		return "Institution";
	}

	@Override
	public String userCacheCategory(int keySet) {
		return getUserCacheCategory();
	}

	@Override
	public String userCacheStringKey(int keySet) {
		return null;
	}

	@Override
	public int userCacheIntegerKey(int keySet) {
		return ucn;
	}
	
	@Override
	public int userCacheKeyCount() {
		return 1;
	}
}
