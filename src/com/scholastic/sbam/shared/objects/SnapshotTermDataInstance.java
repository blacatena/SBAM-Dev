package com.scholastic.sbam.shared.objects;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.scholastic.sbam.shared.util.AppConstants;

public class SnapshotTermDataInstance implements BeanModelTag, IsSerializable {
	protected int		snapshotId;
	protected int		agreementId;
	protected int		termId;
	protected String	productCode;
	protected String	serviceCode;
	protected int		ucn;
	protected int		ucnSuffix;
	protected int		rowId;
	protected Date		startDate;
	protected Date		endDate;
	protected Date		terminateDate;
	protected String	termType;
	protected String	cancelReasonCode;
	protected Date		cancelDate;
	protected double	dollarValue;
	protected int		workStations;
	protected int		buildings;
	protected int		population;
	protected int		enrollment;
	protected String	commissionCode;
	protected char		primaryTerm;
	protected double	serviceFraction		=	1.0d;
	protected double	ucnFraction			=	1.0d;
	
	protected ProductInstance		product;
	protected ServiceInstance		service;
	protected InstitutionInstance	institution;
	protected CancelReasonInstance	cancelReason;
	
	public SnapshotTermDataInstance() {
		super();
	}

	public int getSnapshotId() {
		return snapshotId;
	}

	public void setSnapshotId(int snapshotId) {
		this.snapshotId = snapshotId;
	}

	public int getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(int agreementId) {
		this.agreementId = agreementId;
	}

	public int getTermId() {
		return termId;
	}

	public void setTermId(int termId) {
		this.termId = termId;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public int getUcn() {
		return ucn;
	}

	public void setUcn(int ucn) {
		this.ucn = ucn;
	}

	public int getUcnSuffix() {
		return ucnSuffix;
	}

	public void setUcnSuffix(int ucnSuffix) {
		this.ucnSuffix = ucnSuffix;
	}

	public int getRowId() {
		return rowId;
	}

	public void setRowId(int rowId) {
		this.rowId = rowId;
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

	public String getCancelReasonCode() {
		return cancelReasonCode;
	}

	public void setCancelReasonCode(String cancelReasonCode) {
		this.cancelReasonCode = cancelReasonCode;
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

	public int getWorkStations() {
		return workStations;
	}

	public void setWorkStations(int workStations) {
		this.workStations = workStations;
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

	public String getCommissionCode() {
		return commissionCode;
	}

	public void setCommissionCode(String commissionCode) {
		this.commissionCode = commissionCode;
	}

	public char getPrimaryTerm() {
		return primaryTerm;
	}

	public void setPrimaryTerm(char primaryTerm) {
		this.primaryTerm = primaryTerm;
	}
	
	/* Attached Instance Values */

	public double getServiceFraction() {
		return serviceFraction;
	}

	public void setServiceFraction(double serviceFraction) {
		this.serviceFraction = serviceFraction;
	}

	public double getUcnFraction() {
		return ucnFraction;
	}

	public void setUcnFraction(double ucnFraction) {
		this.ucnFraction = ucnFraction;
	}

	public CancelReasonInstance getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(CancelReasonInstance cancelReason) {
		this.cancelReason = cancelReason;
	}

	public ProductInstance getProduct() {
		return product;
	}

	public void setProduct(ProductInstance product) {
		this.product = product;
	}

	public ServiceInstance getService() {
		return service;
	}

	public void setService(ServiceInstance service) {
		this.service = service;
	}

	public InstitutionInstance getInstitution() {
		return institution;
	}

	public void setInstitution(InstitutionInstance institution) {
		this.institution = institution;
	}
	
	/* COMPUTED VALUES */
	
	public int getAgreementIdCheckDigit() {
		return AppConstants.appendCheckDigit(agreementId);
	}
	
	public double getDollarFraction() {
		return dollarValue * ucnFraction * serviceFraction;
	}
	
	public double getDollarServiceFraction() {
		return dollarValue * serviceFraction;
	}
	
	public double getDollarUcnFraction() {
		return dollarValue * serviceFraction;
	}
	
	
}
