package com.scholastic.sbam.server.database.codegen;

// Generated Jun 28, 2011 11:14:17 AM by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * ProxyIp generated by hbm2java
 */
public class ProxyIp implements java.io.Serializable {

	private ProxyIpId id;
	private long ipLo;
	private long ipHi;
	private String ipRangeCode;
	private char approved;
	private String note;
	private Date createdDatetime;
	private char status;

	public ProxyIp() {
	}

	public ProxyIp(ProxyIpId id, long ipLo, long ipHi, String ipRangeCode,
			char approved, String note, Date createdDatetime, char status) {
		this.id = id;
		this.ipLo = ipLo;
		this.ipHi = ipHi;
		this.ipRangeCode = ipRangeCode;
		this.approved = approved;
		this.note = note;
		this.createdDatetime = createdDatetime;
		this.status = status;
	}

	public ProxyIpId getId() {
		return this.id;
	}

	public void setId(ProxyIpId id) {
		this.id = id;
	}

	public long getIpLo() {
		return this.ipLo;
	}

	public void setIpLo(long ipLo) {
		this.ipLo = ipLo;
	}

	public long getIpHi() {
		return this.ipHi;
	}

	public void setIpHi(long ipHi) {
		this.ipHi = ipHi;
	}

	public String getIpRangeCode() {
		return this.ipRangeCode;
	}

	public void setIpRangeCode(String ipRangeCode) {
		this.ipRangeCode = ipRangeCode;
	}

	public char getApproved() {
		return this.approved;
	}

	public void setApproved(char approved) {
		this.approved = approved;
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
