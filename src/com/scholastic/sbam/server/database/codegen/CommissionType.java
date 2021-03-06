package com.scholastic.sbam.server.database.codegen;

// Generated Feb 1, 2012 5:33:22 PM by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * CommissionType generated by hbm2java
 */
public class CommissionType implements java.io.Serializable {

	private String commissionCode;
	private String description;
	private String shortName;
	private char products;
	private char sites;
	private char agreements;
	private char agreementTerms;
	private char status;
	private Date createdDatetime;

	public CommissionType() {
	}

	public CommissionType(String commissionCode, String description,
			String shortName, char products, char sites, char agreements,
			char agreementTerms, char status, Date createdDatetime) {
		this.commissionCode = commissionCode;
		this.description = description;
		this.shortName = shortName;
		this.products = products;
		this.sites = sites;
		this.agreements = agreements;
		this.agreementTerms = agreementTerms;
		this.status = status;
		this.createdDatetime = createdDatetime;
	}

	public String getCommissionCode() {
		return this.commissionCode;
	}

	public void setCommissionCode(String commissionCode) {
		this.commissionCode = commissionCode;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getShortName() {
		return this.shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public char getProducts() {
		return this.products;
	}

	public void setProducts(char products) {
		this.products = products;
	}

	public char getSites() {
		return this.sites;
	}

	public void setSites(char sites) {
		this.sites = sites;
	}

	public char getAgreements() {
		return this.agreements;
	}

	public void setAgreements(char agreements) {
		this.agreements = agreements;
	}

	public char getAgreementTerms() {
		return this.agreementTerms;
	}

	public void setAgreementTerms(char agreementTerms) {
		this.agreementTerms = agreementTerms;
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
