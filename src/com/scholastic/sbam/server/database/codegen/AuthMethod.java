package com.scholastic.sbam.server.database.codegen;

// Generated Feb 1, 2012 5:33:22 PM by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * AuthMethod generated by hbm2java
 */
public class AuthMethod implements java.io.Serializable {

	private AuthMethodId id;
	private int forUcn;
	private int forUcnSuffix;
	private String forSiteLocCode;
	private String url;
	private String userId;
	private long ipLo;
	private long ipHi;
	private String ipRangeCode;
	private String password;
	private int proxyId;
	private char userType;
	private char remote;
	private char approved;
	private char validated;
	private char activated;
	private String orgPath;
	private String note;
	private Date activatedDatetime;
	private Date deactivatedDatetime;
	private Date reactivatedDatetime;
	private Date updatedDatetime;
	private Date createdDatetime;
	private char status;

	public AuthMethod() {
	}

	public AuthMethod(AuthMethodId id, int forUcn, int forUcnSuffix,
			String forSiteLocCode, String url, String userId, long ipLo,
			long ipHi, String ipRangeCode, String password, int proxyId,
			char userType, char remote, char approved, char validated,
			char activated, String orgPath, String note, Date updatedDatetime,
			Date createdDatetime, char status) {
		this.id = id;
		this.forUcn = forUcn;
		this.forUcnSuffix = forUcnSuffix;
		this.forSiteLocCode = forSiteLocCode;
		this.url = url;
		this.userId = userId;
		this.ipLo = ipLo;
		this.ipHi = ipHi;
		this.ipRangeCode = ipRangeCode;
		this.password = password;
		this.proxyId = proxyId;
		this.userType = userType;
		this.remote = remote;
		this.approved = approved;
		this.validated = validated;
		this.activated = activated;
		this.orgPath = orgPath;
		this.note = note;
		this.updatedDatetime = updatedDatetime;
		this.createdDatetime = createdDatetime;
		this.status = status;
	}

	public AuthMethod(AuthMethodId id, int forUcn, int forUcnSuffix,
			String forSiteLocCode, String url, String userId, long ipLo,
			long ipHi, String ipRangeCode, String password, int proxyId,
			char userType, char remote, char approved, char validated,
			char activated, String orgPath, String note,
			Date activatedDatetime, Date deactivatedDatetime,
			Date reactivatedDatetime, Date updatedDatetime,
			Date createdDatetime, char status) {
		this.id = id;
		this.forUcn = forUcn;
		this.forUcnSuffix = forUcnSuffix;
		this.forSiteLocCode = forSiteLocCode;
		this.url = url;
		this.userId = userId;
		this.ipLo = ipLo;
		this.ipHi = ipHi;
		this.ipRangeCode = ipRangeCode;
		this.password = password;
		this.proxyId = proxyId;
		this.userType = userType;
		this.remote = remote;
		this.approved = approved;
		this.validated = validated;
		this.activated = activated;
		this.orgPath = orgPath;
		this.note = note;
		this.activatedDatetime = activatedDatetime;
		this.deactivatedDatetime = deactivatedDatetime;
		this.reactivatedDatetime = reactivatedDatetime;
		this.updatedDatetime = updatedDatetime;
		this.createdDatetime = createdDatetime;
		this.status = status;
	}

	public AuthMethodId getId() {
		return this.id;
	}

	public void setId(AuthMethodId id) {
		this.id = id;
	}

	public int getForUcn() {
		return this.forUcn;
	}

	public void setForUcn(int forUcn) {
		this.forUcn = forUcn;
	}

	public int getForUcnSuffix() {
		return this.forUcnSuffix;
	}

	public void setForUcnSuffix(int forUcnSuffix) {
		this.forUcnSuffix = forUcnSuffix;
	}

	public String getForSiteLocCode() {
		return this.forSiteLocCode;
	}

	public void setForSiteLocCode(String forSiteLocCode) {
		this.forSiteLocCode = forSiteLocCode;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getProxyId() {
		return this.proxyId;
	}

	public void setProxyId(int proxyId) {
		this.proxyId = proxyId;
	}

	public char getUserType() {
		return this.userType;
	}

	public void setUserType(char userType) {
		this.userType = userType;
	}

	public char getRemote() {
		return this.remote;
	}

	public void setRemote(char remote) {
		this.remote = remote;
	}

	public char getApproved() {
		return this.approved;
	}

	public void setApproved(char approved) {
		this.approved = approved;
	}

	public char getValidated() {
		return this.validated;
	}

	public void setValidated(char validated) {
		this.validated = validated;
	}

	public char getActivated() {
		return this.activated;
	}

	public void setActivated(char activated) {
		this.activated = activated;
	}

	public String getOrgPath() {
		return this.orgPath;
	}

	public void setOrgPath(String orgPath) {
		this.orgPath = orgPath;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Date getActivatedDatetime() {
		return this.activatedDatetime;
	}

	public void setActivatedDatetime(Date activatedDatetime) {
		this.activatedDatetime = activatedDatetime;
	}

	public Date getDeactivatedDatetime() {
		return this.deactivatedDatetime;
	}

	public void setDeactivatedDatetime(Date deactivatedDatetime) {
		this.deactivatedDatetime = deactivatedDatetime;
	}

	public Date getReactivatedDatetime() {
		return this.reactivatedDatetime;
	}

	public void setReactivatedDatetime(Date reactivatedDatetime) {
		this.reactivatedDatetime = reactivatedDatetime;
	}

	public Date getUpdatedDatetime() {
		return this.updatedDatetime;
	}

	public void setUpdatedDatetime(Date updatedDatetime) {
		this.updatedDatetime = updatedDatetime;
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
