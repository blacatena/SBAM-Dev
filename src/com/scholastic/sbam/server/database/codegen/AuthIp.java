package com.scholastic.sbam.server.database.codegen;

// Generated Jul 28, 2011 1:20:31 PM by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * AuthIp generated by hbm2java
 */
public class AuthIp implements java.io.Serializable {

	private AuthIpId id;
	private long ipHi;
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

	public AuthIp() {
	}

	public AuthIp(AuthIpId id, long ipHi, char remote, char approved,
			char validated, char activated, String note, Date createdDatetime,
			Date activatedDatetime, Date deactivatedDatetime,
			Date reactivatedDatetime) {
		this.id = id;
		this.ipHi = ipHi;
		this.remote = remote;
		this.approved = approved;
		this.validated = validated;
		this.activated = activated;
		this.note = note;
		this.createdDatetime = createdDatetime;
		this.activatedDatetime = activatedDatetime;
		this.deactivatedDatetime = deactivatedDatetime;
		this.reactivatedDatetime = reactivatedDatetime;
	}

	public AuthIp(AuthIpId id, long ipHi, char remote, char approved,
			char validated, char activated, String note, Date createdDatetime,
			Date updatedDatetime, Date activatedDatetime,
			Date deactivatedDatetime, Date reactivatedDatetime) {
		this.id = id;
		this.ipHi = ipHi;
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

	public AuthIpId getId() {
		return this.id;
	}

	public void setId(AuthIpId id) {
		this.id = id;
	}

	public long getIpHi() {
		return this.ipHi;
	}

	public void setIpHi(long ipHi) {
		this.ipHi = ipHi;
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
