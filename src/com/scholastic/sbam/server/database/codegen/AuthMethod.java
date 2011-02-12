package com.scholastic.sbam.server.database.codegen;

// Generated Feb 11, 2011 8:34:07 PM by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * AuthMethod generated by hbm2java
 */
public class AuthMethod implements java.io.Serializable {

	private AuthMethodId id;
	private Integer ipTo;
	private String password;
	private Integer proxyId;
	private Character userType;
	private char remote;
	private char approved;
	private char validated;
	private char activated;
	private String note;
	private Date createdDatetime;
	private Date updatedDatetime;
	private Date activatedDatetime;
	private Date deactivatedDatetime;
	private Date reactivatedDatetime;

	public AuthMethod() {
	}

	public AuthMethod(AuthMethodId id, char remote, char approved,
			char validated, char activated, String note, Date createdDatetime,
			Date updatedDatetime, Date activatedDatetime,
			Date deactivatedDatetime, Date reactivatedDatetime) {
		this.id = id;
		this.remote = remote;
		this.approved = approved;
		this.validated = validated;
		this.activated = activated;
		this.note = note;
		this.createdDatetime = createdDatetime;
		this.updatedDatetime = updatedDatetime;
		this.activatedDatetime = activatedDatetime;
		this.deactivatedDatetime = deactivatedDatetime;
		this.reactivatedDatetime = reactivatedDatetime;
	}

	public AuthMethod(AuthMethodId id, Integer ipTo, String password,
			Integer proxyId, Character userType, char remote, char approved,
			char validated, char activated, String note, Date createdDatetime,
			Date updatedDatetime, Date activatedDatetime,
			Date deactivatedDatetime, Date reactivatedDatetime) {
		this.id = id;
		this.ipTo = ipTo;
		this.password = password;
		this.proxyId = proxyId;
		this.userType = userType;
		this.remote = remote;
		this.approved = approved;
		this.validated = validated;
		this.activated = activated;
		this.note = note;
		this.createdDatetime = createdDatetime;
		this.updatedDatetime = updatedDatetime;
		this.activatedDatetime = activatedDatetime;
		this.deactivatedDatetime = deactivatedDatetime;
		this.reactivatedDatetime = reactivatedDatetime;
	}

	public AuthMethodId getId() {
		return this.id;
	}

	public void setId(AuthMethodId id) {
		this.id = id;
	}

	public Integer getIpTo() {
		return this.ipTo;
	}

	public void setIpTo(Integer ipTo) {
		this.ipTo = ipTo;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getProxyId() {
		return this.proxyId;
	}

	public void setProxyId(Integer proxyId) {
		this.proxyId = proxyId;
	}

	public Character getUserType() {
		return this.userType;
	}

	public void setUserType(Character userType) {
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

	public Date getUpdatedDatetime() {
		return this.updatedDatetime;
	}

	public void setUpdatedDatetime(Date updatedDatetime) {
		this.updatedDatetime = updatedDatetime;
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

}
