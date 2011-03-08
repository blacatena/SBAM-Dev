package com.scholastic.sbam.shared.objects;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class AgreementTermInstance extends BetterRowEditInstance implements BeanModelTag, IsSerializable {

	private int		agreementId;
	private int		id;
	
	private String	productCode;
	private String	productDescription;
	private String	productShortName;
	private Date	startDate;
	private Date	endDate;
	private Date	terminateDate;
	private String	termType;
	private String	termTypeDescription;
	
	private String	commissionCode;
	private String	commissionCodeDescription;

	private String	cancelReasonCode;
	private String	cancelReasonDescription;
	private Date	cancelDate;
	
	private double	dollarValue;
	private int		workstations;
	private int		buildings;
	private int		population;
	private int		enrollment;
	
	private String	poNumber;
	private int		referenceSaId;
	
	private String	orgPath;
	private String	primaryOrgPath;
	private char	primary;
	private boolean primaryTerm;
	
	private String	note;
	
	private char	status;
	private boolean	active;
	private Date	createdDatetime;
	
	@Override
	public void markForDeletion() {
		setStatus('X');
	}

	@Override
	public boolean thisIsDeleted() {
		return status == 'X';
	}

	@Override
	public boolean thisIsValid() {
		return true;
	}

	@Override
	public String returnTriggerProperty() {
		return "junk";
	}

	@Override
	public String returnTriggerValue() {
		return "junk";
	}

	public char getStatus() {
		return status;
	}

	public void setStatus(char status) {
		this.status = status;
		this.active = (this.status == 'A');
	}

	public Date getCreatedDatetime() {
		return createdDatetime;
	}

	public void setCreatedDatetime(Date createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		if (this.status == 'X')
			return;
		setStatus(active?'A':'I');
	}

	public int getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(int agreementId) {
		this.agreementId = agreementId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
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

	public String getTermType() {
		return termType;
	}

	public void setTermType(String termType) {
		this.termType = termType;
	}

	public String getTermTypeDescription() {
		return termTypeDescription;
	}

	public void setTermTypeDescription(String termTypeDescription) {
		this.termTypeDescription = termTypeDescription;
	}

	public String getCommissionCode() {
		return commissionCode;
	}

	public void setCommissionCode(String commissionCode) {
		this.commissionCode = commissionCode;
	}

	public String getCommissionCodeDescription() {
		return commissionCodeDescription;
	}

	public void setCommissionCodeDescription(String commissionCodeDescription) {
		this.commissionCodeDescription = commissionCodeDescription;
	}

	public String getCancelReasonCode() {
		return cancelReasonCode;
	}

	public void setCancelReasonCode(String cancelReasonCode) {
		this.cancelReasonCode = cancelReasonCode;
	}

	public String getCancelReasonDescription() {
		return cancelReasonDescription;
	}

	public void setCancelReasonDescription(String cancelReasonDescription) {
		this.cancelReasonDescription = cancelReasonDescription;
	}

	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	public double getDollarValue() {
		return dollarValue;
	}

	public void setDollarValue(double dollarValue) {
		this.dollarValue = dollarValue;
	}

	public int getWorkstations() {
		return workstations;
	}

	public void setWorkstations(int workstations) {
		this.workstations = workstations;
	}

	public int getBuildings() {
		return buildings;
	}

	public void setBuildings(int buildings) {
		this.buildings = buildings;
	}

	public int getPopulation() {
		return population;
	}

	public void setPopulation(int population) {
		this.population = population;
	}

	public int getEnrollment() {
		return enrollment;
	}

	public void setEnrollment(int enrollment) {
		this.enrollment = enrollment;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public int getReferenceSaId() {
		return referenceSaId;
	}

	public void setReferenceSaId(int referenceSaId) {
		this.referenceSaId = referenceSaId;
	}

	public String getOrgPath() {
		return orgPath;
	}

	public void setOrgPath(String orgPath) {
		this.orgPath = orgPath;
	}

	public String getPrimaryOrgPath() {
		return primaryOrgPath;
	}

	public void setPrimaryOrgPath(String primaryOrgPath) {
		this.primaryOrgPath = primaryOrgPath;
	}

	public char getPrimary() {
		return primary;
	}

	public void setPrimary(char primary) {
		this.primary = primary;
		this.primaryTerm = (primary == 'y');
	}

	public boolean isPrimaryTerm() {
		return primaryTerm;
	}

	public void setPrimaryTerm(boolean primaryTerm) {
		setPrimary(primaryTerm ? 'y' : 'n');
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public String getProductShortName() {
		return productShortName;
	}

	public void setProductShortName(String productShortName) {
		this.productShortName = productShortName;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String toString() {
		return "Term " + agreementId + "-" + id;
	}
}
