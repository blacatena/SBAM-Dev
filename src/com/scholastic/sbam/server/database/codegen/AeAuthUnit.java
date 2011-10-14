package com.scholastic.sbam.server.database.codegen;

// Generated Oct 14, 2011 9:50:42 AM by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * AeAuthUnit generated by hbm2java
 */
public class AeAuthUnit implements java.io.Serializable {

	private Integer auId;
	private int siteUcn;
	private int siteUcnSuffix;
	private String siteLocCode;
	private int billUcn;
	private int billUcnSuffix;
	private int siteParentUcn;
	private int siteParentUcnSuffix;
	private Date createdDatetime;

	public AeAuthUnit() {
	}

	public AeAuthUnit(int siteUcn, int siteUcnSuffix, String siteLocCode,
			int billUcn, int billUcnSuffix, int siteParentUcn,
			int siteParentUcnSuffix, Date createdDatetime) {
		this.siteUcn = siteUcn;
		this.siteUcnSuffix = siteUcnSuffix;
		this.siteLocCode = siteLocCode;
		this.billUcn = billUcn;
		this.billUcnSuffix = billUcnSuffix;
		this.siteParentUcn = siteParentUcn;
		this.siteParentUcnSuffix = siteParentUcnSuffix;
		this.createdDatetime = createdDatetime;
	}

	public Integer getAuId() {
		return this.auId;
	}

	public void setAuId(Integer auId) {
		this.auId = auId;
	}

	public int getSiteUcn() {
		return this.siteUcn;
	}

	public void setSiteUcn(int siteUcn) {
		this.siteUcn = siteUcn;
	}

	public int getSiteUcnSuffix() {
		return this.siteUcnSuffix;
	}

	public void setSiteUcnSuffix(int siteUcnSuffix) {
		this.siteUcnSuffix = siteUcnSuffix;
	}

	public String getSiteLocCode() {
		return this.siteLocCode;
	}

	public void setSiteLocCode(String siteLocCode) {
		this.siteLocCode = siteLocCode;
	}

	public int getBillUcn() {
		return this.billUcn;
	}

	public void setBillUcn(int billUcn) {
		this.billUcn = billUcn;
	}

	public int getBillUcnSuffix() {
		return this.billUcnSuffix;
	}

	public void setBillUcnSuffix(int billUcnSuffix) {
		this.billUcnSuffix = billUcnSuffix;
	}

	public int getSiteParentUcn() {
		return this.siteParentUcn;
	}

	public void setSiteParentUcn(int siteParentUcn) {
		this.siteParentUcn = siteParentUcn;
	}

	public int getSiteParentUcnSuffix() {
		return this.siteParentUcnSuffix;
	}

	public void setSiteParentUcnSuffix(int siteParentUcnSuffix) {
		this.siteParentUcnSuffix = siteParentUcnSuffix;
	}

	public Date getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Date createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

}
