package com.scholastic.sbam.server.database.codegen;

// Generated Feb 1, 2012 5:33:22 PM by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * Proxy generated by hbm2java
 */
public class Proxy implements java.io.Serializable {

	private Integer proxyId;
	private int idCheckDigit;
	private String description;
	private String searchKeys;
	private String note;
	private Date createdDatetime;
	private char status;

	public Proxy() {
	}

	public Proxy(int idCheckDigit, String description, String searchKeys,
			String note, Date createdDatetime, char status) {
		this.idCheckDigit = idCheckDigit;
		this.description = description;
		this.searchKeys = searchKeys;
		this.note = note;
		this.createdDatetime = createdDatetime;
		this.status = status;
	}

	public Integer getProxyId() {
		return this.proxyId;
	}

	public void setProxyId(Integer proxyId) {
		this.proxyId = proxyId;
	}

	public int getIdCheckDigit() {
		return this.idCheckDigit;
	}

	public void setIdCheckDigit(int idCheckDigit) {
		this.idCheckDigit = idCheckDigit;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSearchKeys() {
		return this.searchKeys;
	}

	public void setSearchKeys(String searchKeys) {
		this.searchKeys = searchKeys;
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
