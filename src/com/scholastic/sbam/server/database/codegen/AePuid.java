package com.scholastic.sbam.server.database.codegen;

// Generated Jul 28, 2011 1:20:31 PM by Hibernate Tools 3.2.4.GA

/**
 * AePuid generated by hbm2java
 */
public class AePuid implements java.io.Serializable {

	private AePuidId id;
	private String password;
	private char remote;
	private char userType;
	private long ipLo;
	private long ipHi;
	private String ipRangeCode;

	public AePuid() {
	}

	public AePuid(AePuidId id, String password, char remote, char userType,
			long ipLo, long ipHi, String ipRangeCode) {
		this.id = id;
		this.password = password;
		this.remote = remote;
		this.userType = userType;
		this.ipLo = ipLo;
		this.ipHi = ipHi;
		this.ipRangeCode = ipRangeCode;
	}

	public AePuidId getId() {
		return this.id;
	}

	public void setId(AePuidId id) {
		this.id = id;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public char getRemote() {
		return this.remote;
	}

	public void setRemote(char remote) {
		this.remote = remote;
	}

	public char getUserType() {
		return this.userType;
	}

	public void setUserType(char userType) {
		this.userType = userType;
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

}
