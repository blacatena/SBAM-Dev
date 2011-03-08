package com.scholastic.sbam.shared.objects;

import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * An instance of data meant to summarize the primary content of interest for an agreement.
 * 
 * This object does not correlate on a one-to-one aspect to anything in the database, and hence is an abstract entity.
 *  
 * @author Bob Lacatena
 *
 */
public class AgreementSummaryInstance implements BeanModelTag, IsSerializable {
	private int		id;
	private int		idCheckDigit;
	private Date	createdDate;
	private Date	firstStartDate;
	private Date	lastStartDate;
	private Date	endDate;
	private Date	terminateDate;
	private char	status;
	private String	deleteReasonCode;
	private String	commissionCode;
	private String	agreementTypeCode;
	private int		billUcn;
	/**
	 * This denotes whether or not only primary services were used in constructing the summary instance.
	 */
	private boolean fromPrimaryOnly;
	/**
	 * This denotes whether or not the instance was constructed from a site search (versus a bill UCN search)
	 */
	private boolean fromSiteSearch;
	
	//	Extended data (not always loaded)
	private int		siteCount;
	private int		methodCount;
	private int		userIdCount;
	private int		urlCount;
	private int		ipAddressCount;
	private int		servicesCount;
	
	private List<String> services;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
		idCheckDigit = AppConstants.appendCheckDigit(id);
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getFirstStartDate() {
		return firstStartDate;
	}

	public void setFirstStartDate(Date firstStartDate) {
		this.firstStartDate = firstStartDate;
	}

	public Date getLastStartDate() {
		return lastStartDate;
	}

	public void setLastStartDate(Date lastStartDate) {
		this.lastStartDate = lastStartDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getTerminateDate() {
		return terminateDate;
	}

	public void setTerminateDate(Date terminateDate) {
		this.terminateDate = terminateDate;
	}

	public char getStatus() {
		return status;
	}

	public void setStatus(char status) {
		this.status = status;
	}

	public String getDeleteReasonCode() {
		return deleteReasonCode;
	}

	public void setDeleteReasonCode(String deleteReasonCode) {
		this.deleteReasonCode = deleteReasonCode;
	}

	public String getCommissionCode() {
		return commissionCode;
	}

	public void setCommissionCode(String commissionCode) {
		this.commissionCode = commissionCode;
	}

	public String getAgreementTypeCode() {
		return agreementTypeCode;
	}

	public void setAgreementTypeCode(String agreementTypeCode) {
		this.agreementTypeCode = agreementTypeCode;
	}

	public int getBillUcn() {
		return billUcn;
	}

	public void setBillUcn(int billUcn) {
		this.billUcn = billUcn;
	}

	public int getSiteCount() {
		return siteCount;
	}

	public void setSiteCount(int siteCount) {
		this.siteCount = siteCount;
	}

	public int getMethodCount() {
		return methodCount;
	}

	public void setMethodCount(int methodCount) {
		this.methodCount = methodCount;
	}

	public int getUserIdCount() {
		return userIdCount;
	}

	public void setUserIdCount(int userIdCount) {
		this.userIdCount = userIdCount;
	}

	public int getUrlCount() {
		return urlCount;
	}

	public void setUrlCount(int urlCount) {
		this.urlCount = urlCount;
	}

	public int getIpAddressCount() {
		return ipAddressCount;
	}

	public void setIpAddressCount(int ipAddressCount) {
		this.ipAddressCount = ipAddressCount;
	}

	public int getServicesCount() {
		return servicesCount;
	}

	public void setServicesCount(int servicesCount) {
		this.servicesCount = servicesCount;
	}

	public List<String> getServices() {
		return services;
	}

	public void setServices(List<String> services) {
		this.services = services;
	}

	public boolean isFromPrimaryOnly() {
		return fromPrimaryOnly;
	}

	public void setFromPrimaryOnly(boolean fromPrimaryOnly) {
		this.fromPrimaryOnly = fromPrimaryOnly;
	}

	public boolean isFromSiteSearch() {
		return fromSiteSearch;
	}

	public void setFromSiteSearch(boolean fromSiteSearch) {
		this.fromSiteSearch = fromSiteSearch;
	}

	public int getIdCheckDigit() {
		return idCheckDigit;
	}

	public void setIdCheckDigit(int idCheckDigit) {
		this.idCheckDigit = idCheckDigit;
		this.id = idCheckDigit / 10;
	}
	
	
}
