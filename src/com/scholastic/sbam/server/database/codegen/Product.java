package com.scholastic.sbam.server.database.codegen;

// Generated Jul 14, 2011 10:05:06 PM by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * Product generated by hbm2java
 */
public class Product implements java.io.Serializable {

	private String productCode;
	private String description;
	private String shortName;
	private String defaultTermType;
	private String defaultCommissionCode;
	private Date createdDatetime;
	private char status;

	public Product() {
	}

	public Product(String productCode, String description, String shortName,
			String defaultCommissionCode, Date createdDatetime, char status) {
		this.productCode = productCode;
		this.description = description;
		this.shortName = shortName;
		this.defaultCommissionCode = defaultCommissionCode;
		this.createdDatetime = createdDatetime;
		this.status = status;
	}

	public Product(String productCode, String description, String shortName,
			String defaultTermType, String defaultCommissionCode,
			Date createdDatetime, char status) {
		this.productCode = productCode;
		this.description = description;
		this.shortName = shortName;
		this.defaultTermType = defaultTermType;
		this.defaultCommissionCode = defaultCommissionCode;
		this.createdDatetime = createdDatetime;
		this.status = status;
	}

	public String getProductCode() {
		return this.productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
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

	public String getDefaultTermType() {
		return this.defaultTermType;
	}

	public void setDefaultTermType(String defaultTermType) {
		this.defaultTermType = defaultTermType;
	}

	public String getDefaultCommissionCode() {
		return this.defaultCommissionCode;
	}

	public void setDefaultCommissionCode(String defaultCommissionCode) {
		this.defaultCommissionCode = defaultCommissionCode;
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
