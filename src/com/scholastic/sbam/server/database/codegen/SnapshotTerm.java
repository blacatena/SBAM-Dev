package com.scholastic.sbam.server.database.codegen;

// Generated Jun 28, 2011 11:14:17 AM by Hibernate Tools 3.2.4.GA

import java.math.BigDecimal;
import java.util.Date;

/**
 * SnapshotTerm generated by hbm2java
 */
public class SnapshotTerm implements java.io.Serializable {

	private SnapshotTermId id;
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
	private String commissionCode;
	private char primaryTerm;
	private long serviceFraction;
	private long customerFraction;

	public SnapshotTerm() {
	}

	public SnapshotTerm(SnapshotTermId id, String termType,
			String cancelReasonCode, BigDecimal dollarValue, int workstations,
			int buildings, int population, int enrollment,
			String commissionCode, char primaryTerm, long serviceFraction,
			long customerFraction) {
		this.id = id;
		this.termType = termType;
		this.cancelReasonCode = cancelReasonCode;
		this.dollarValue = dollarValue;
		this.workstations = workstations;
		this.buildings = buildings;
		this.population = population;
		this.enrollment = enrollment;
		this.commissionCode = commissionCode;
		this.primaryTerm = primaryTerm;
		this.serviceFraction = serviceFraction;
		this.customerFraction = customerFraction;
	}

	public SnapshotTerm(SnapshotTermId id, Date startDate, Date endDate,
			Date terminateDate, String termType, String cancelReasonCode,
			Date cancelDate, BigDecimal dollarValue, int workstations,
			int buildings, int population, int enrollment,
			String commissionCode, char primaryTerm, long serviceFraction,
			long customerFraction) {
		this.id = id;
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
		this.commissionCode = commissionCode;
		this.primaryTerm = primaryTerm;
		this.serviceFraction = serviceFraction;
		this.customerFraction = customerFraction;
	}

	public SnapshotTermId getId() {
		return this.id;
	}

	public void setId(SnapshotTermId id) {
		this.id = id;
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

	public String getCommissionCode() {
		return this.commissionCode;
	}

	public void setCommissionCode(String commissionCode) {
		this.commissionCode = commissionCode;
	}

	public char getPrimaryTerm() {
		return this.primaryTerm;
	}

	public void setPrimaryTerm(char primaryTerm) {
		this.primaryTerm = primaryTerm;
	}

	public long getServiceFraction() {
		return this.serviceFraction;
	}

	public void setServiceFraction(long serviceFraction) {
		this.serviceFraction = serviceFraction;
	}

	public long getCustomerFraction() {
		return this.customerFraction;
	}

	public void setCustomerFraction(long customerFraction) {
		this.customerFraction = customerFraction;
	}

}
