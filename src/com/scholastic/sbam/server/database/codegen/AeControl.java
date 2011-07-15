package com.scholastic.sbam.server.database.codegen;

// Generated Jul 14, 2011 10:05:06 PM by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * AeControl generated by hbm2java
 */
public class AeControl implements java.io.Serializable {

	private Integer aeId;
	private Date asOfDate;
	private Date initiatedDatetime;
	private Date terminatedDatetime;
	private Date completedDatetime;
	private int elapsedSeconds;
	private int countAgreements;
	private int countSites;
	private int countIps;
	private int countUids;
	private int countProxyUids;
	private int countUrls;
	private int countProducts;
	private int countErrors;
	private char ucnMode;
	private char status;
	private Date createdDatetime;

	public AeControl() {
	}

	public AeControl(Date asOfDate, Date initiatedDatetime, int elapsedSeconds,
			int countAgreements, int countSites, int countIps, int countUids,
			int countProxyUids, int countUrls, int countProducts,
			int countErrors, char ucnMode, char status, Date createdDatetime) {
		this.asOfDate = asOfDate;
		this.initiatedDatetime = initiatedDatetime;
		this.elapsedSeconds = elapsedSeconds;
		this.countAgreements = countAgreements;
		this.countSites = countSites;
		this.countIps = countIps;
		this.countUids = countUids;
		this.countProxyUids = countProxyUids;
		this.countUrls = countUrls;
		this.countProducts = countProducts;
		this.countErrors = countErrors;
		this.ucnMode = ucnMode;
		this.status = status;
		this.createdDatetime = createdDatetime;
	}

	public AeControl(Date asOfDate, Date initiatedDatetime,
			Date terminatedDatetime, Date completedDatetime,
			int elapsedSeconds, int countAgreements, int countSites,
			int countIps, int countUids, int countProxyUids, int countUrls,
			int countProducts, int countErrors, char ucnMode, char status,
			Date createdDatetime) {
		this.asOfDate = asOfDate;
		this.initiatedDatetime = initiatedDatetime;
		this.terminatedDatetime = terminatedDatetime;
		this.completedDatetime = completedDatetime;
		this.elapsedSeconds = elapsedSeconds;
		this.countAgreements = countAgreements;
		this.countSites = countSites;
		this.countIps = countIps;
		this.countUids = countUids;
		this.countProxyUids = countProxyUids;
		this.countUrls = countUrls;
		this.countProducts = countProducts;
		this.countErrors = countErrors;
		this.ucnMode = ucnMode;
		this.status = status;
		this.createdDatetime = createdDatetime;
	}

	public Integer getAeId() {
		return this.aeId;
	}

	public void setAeId(Integer aeId) {
		this.aeId = aeId;
	}

	public Date getAsOfDate() {
		return this.asOfDate;
	}

	public void setAsOfDate(Date asOfDate) {
		this.asOfDate = asOfDate;
	}

	public Date getInitiatedDatetime() {
		return this.initiatedDatetime;
	}

	public void setInitiatedDatetime(Date initiatedDatetime) {
		this.initiatedDatetime = initiatedDatetime;
	}

	public Date getTerminatedDatetime() {
		return this.terminatedDatetime;
	}

	public void setTerminatedDatetime(Date terminatedDatetime) {
		this.terminatedDatetime = terminatedDatetime;
	}

	public Date getCompletedDatetime() {
		return this.completedDatetime;
	}

	public void setCompletedDatetime(Date completedDatetime) {
		this.completedDatetime = completedDatetime;
	}

	public int getElapsedSeconds() {
		return this.elapsedSeconds;
	}

	public void setElapsedSeconds(int elapsedSeconds) {
		this.elapsedSeconds = elapsedSeconds;
	}

	public int getCountAgreements() {
		return this.countAgreements;
	}

	public void setCountAgreements(int countAgreements) {
		this.countAgreements = countAgreements;
	}

	public int getCountSites() {
		return this.countSites;
	}

	public void setCountSites(int countSites) {
		this.countSites = countSites;
	}

	public int getCountIps() {
		return this.countIps;
	}

	public void setCountIps(int countIps) {
		this.countIps = countIps;
	}

	public int getCountUids() {
		return this.countUids;
	}

	public void setCountUids(int countUids) {
		this.countUids = countUids;
	}

	public int getCountProxyUids() {
		return this.countProxyUids;
	}

	public void setCountProxyUids(int countProxyUids) {
		this.countProxyUids = countProxyUids;
	}

	public int getCountUrls() {
		return this.countUrls;
	}

	public void setCountUrls(int countUrls) {
		this.countUrls = countUrls;
	}

	public int getCountProducts() {
		return this.countProducts;
	}

	public void setCountProducts(int countProducts) {
		this.countProducts = countProducts;
	}

	public int getCountErrors() {
		return this.countErrors;
	}

	public void setCountErrors(int countErrors) {
		this.countErrors = countErrors;
	}

	public char getUcnMode() {
		return this.ucnMode;
	}

	public void setUcnMode(char ucnMode) {
		this.ucnMode = ucnMode;
	}

	public char getStatus() {
		return this.status;
	}

	public void setStatus(char status) {
		this.status = status;
	}

	public Date getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Date createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

}
