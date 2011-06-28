package com.scholastic.sbam.server.database.codegen;

// Generated Jun 28, 2011 11:14:17 AM by Hibernate Tools 3.2.4.GA

/**
 * AeConflict generated by hbm2java
 */
public class AeConflict implements java.io.Serializable {

	private AeConflictId id;
	private int conflictType;
	private String conflictMsg;
	private String conflictKey;
	private String methodType;
	private int auId;
	private int referenceAuId;
	private long ipLo;
	private long ipHi;
	private String userId;
	private String password;
	private String url;

	public AeConflict() {
	}

	public AeConflict(AeConflictId id, int conflictType, String conflictMsg,
			String conflictKey, String methodType, int auId, int referenceAuId,
			long ipLo, long ipHi, String userId, String password, String url) {
		this.id = id;
		this.conflictType = conflictType;
		this.conflictMsg = conflictMsg;
		this.conflictKey = conflictKey;
		this.methodType = methodType;
		this.auId = auId;
		this.referenceAuId = referenceAuId;
		this.ipLo = ipLo;
		this.ipHi = ipHi;
		this.userId = userId;
		this.password = password;
		this.url = url;
	}

	public AeConflictId getId() {
		return this.id;
	}

	public void setId(AeConflictId id) {
		this.id = id;
	}

	public int getConflictType() {
		return this.conflictType;
	}

	public void setConflictType(int conflictType) {
		this.conflictType = conflictType;
	}

	public String getConflictMsg() {
		return this.conflictMsg;
	}

	public void setConflictMsg(String conflictMsg) {
		this.conflictMsg = conflictMsg;
	}

	public String getConflictKey() {
		return this.conflictKey;
	}

	public void setConflictKey(String conflictKey) {
		this.conflictKey = conflictKey;
	}

	public String getMethodType() {
		return this.methodType;
	}

	public void setMethodType(String methodType) {
		this.methodType = methodType;
	}

	public int getAuId() {
		return this.auId;
	}

	public void setAuId(int auId) {
		this.auId = auId;
	}

	public int getReferenceAuId() {
		return this.referenceAuId;
	}

	public void setReferenceAuId(int referenceAuId) {
		this.referenceAuId = referenceAuId;
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

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
