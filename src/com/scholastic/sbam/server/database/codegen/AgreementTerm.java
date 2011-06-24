package com.scholastic.sbam.server.database.codegen;

// Generated Jun 23, 2011 2:41:16 PM by Hibernate Tools 3.2.4.GA

import java.math.BigDecimal;
import java.util.Date;

/**
 * AgreementTerm generated by hbm2java
 */
public class AgreementTerm implements java.io.Serializable {

	private AgreementTermId id;
	private String productCode;
	private Date startDate;
	private Date endDate;
	private Date terminateDate;
	private String termType;
	private String cancelReasonCode;
	private Date cancelDate;
	private BigDecimal dollarValue;
	private int workstations;
	private int buildings;
	private int population;
	private int enrollment;
	private String poNumber;
	private int referenceSaId;
	private String commissionCode;
	private String orgPath;
	private String primaryOrgPath;
	private char primaryTerm;
	private String note;
	private Date createdDatetime;
	private char status;

	public AgreementTerm() {
	}

	public AgreementTerm(AgreementTermId id, String productCode,
			String termType, String cancelReasonCode, BigDecimal dollarValue,
			int workstations, int buildings, int population, int enrollment,
			String poNumber, int referenceSaId, String commissionCode,
			String orgPath, String primaryOrgPath, char primaryTerm,
			String note, Date createdDatetime, char status) {
		this.id = id;
		this.productCode = productCode;
		this.termType = termType;
		this.cancelReasonCode = cancelReasonCode;
		this.dollarValue = dollarValue;
		this.workstations = workstations;
		this.buildings = buildings;
		this.population = population;
		this.enrollment = enrollment;
		this.poNumber = poNumber;
		this.referenceSaId = referenceSaId;
		this.commissionCode = commissionCode;
		this.orgPath = orgPath;
		this.primaryOrgPath = primaryOrgPath;
		this.primaryTerm = primaryTerm;
		this.note = note;
		this.createdDatetime = createdDatetime;
		this.status = status;
	}

	public AgreementTerm(AgreementTermId id, String productCode,
			Date startDate, Date endDate, Date terminateDate, String termType,
			String cancelReasonCode, Date cancelDate, BigDecimal dollarValue,
			int workstations, int buildings, int population, int enrollment,
			String poNumber, int referenceSaId, String commissionCode,
			String orgPath, String primaryOrgPath, char primaryTerm,
			String note, Date createdDatetime, char status) {
		this.id = id;
		this.productCode = productCode;
		this.startDate = startDate;
		this.endDate = endDate;
		this.terminateDate = terminateDate;
		this.termType = termType;
		this.cancelReasonCode = cancelReasonCode;
		this.cancelDate = cancelDate;
		this.dollarValue = dollarValue;
		this.workstations = workstations;
		this.buildings = buildings;
		this.population = population;
		this.enrollment = enrollment;
		this.poNumber = poNumber;
		this.referenceSaId = referenceSaId;
		this.commissionCode = commissionCode;
		this.orgPath = orgPath;
		this.primaryOrgPath = primaryOrgPath;
		this.primaryTerm = primaryTerm;
		this.note = note;
		this.createdDatetime = createdDatetime;
		this.status = status;
	}

	public AgreementTermId getId() {
		return this.id;
	}

	public void setId(AgreementTermId id) {
		this.id = id;
	}

	public String getProductCode() {
		return this.productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getTerminateDate() {
		return this.terminateDate;
	}

	public void setTerminateDate(Date terminateDate) {
		this.terminateDate = terminateDate;
	}

	public String getTermType() {
		return this.termType;
	}

	public void setTermType(String termType) {
		this.termType = termType;
	}

	public String getCancelReasonCode() {
		return this.cancelReasonCode;
	}

	public void setCancelReasonCode(String cancelReasonCode) {
		this.cancelReasonCode = cancelReasonCode;
	}

	public Date getCancelDate() {
		return this.cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	public BigDecimal getDollarValue() {
		return this.dollarValue;
	}

	public void setDollarValue(BigDecimal dollarValue) {
		this.dollarValue = dollarValue;
	}

	public int getWorkstations() {
		return this.workstations;
	}

	public void setWorkstations(int workstations) {
		this.workstations = workstations;
	}

	public int getBuildings() {
		return this.buildings;
	}

	public void setBuildings(int buildings) {
		this.buildings = buildings;
	}

	public int getPopulation() {
		return this.population;
	}

	public void setPopulation(int population) {
		this.population = population;
	}

	public int getEnrollment() {
		return this.enrollment;
	}

	public void setEnrollment(int enrollment) {
		this.enrollment = enrollment;
	}

	public String getPoNumber() {
		return this.poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public int getReferenceSaId() {
		return this.referenceSaId;
	}

	public void setReferenceSaId(int referenceSaId) {
		this.referenceSaId = referenceSaId;
	}

	public String getCommissionCode() {
		return this.commissionCode;
	}

	public void setCommissionCode(String commissionCode) {
		this.commissionCode = commissionCode;
	}

	public String getOrgPath() {
		return this.orgPath;
	}

	public void setOrgPath(String orgPath) {
		this.orgPath = orgPath;
	}

	public String getPrimaryOrgPath() {
		return this.primaryOrgPath;
	}

	public void setPrimaryOrgPath(String primaryOrgPath) {
		this.primaryOrgPath = primaryOrgPath;
	}

	public char getPrimaryTerm() {
		return this.primaryTerm;
	}

	public void setPrimaryTerm(char primaryTerm) {
		this.primaryTerm = primaryTerm;
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
