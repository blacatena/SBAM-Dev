package com.scholastic.sbam.server.database.codegen;

// Generated Oct 14, 2011 9:50:42 AM by Hibernate Tools 3.2.4.GA

/**
 * AeUid generated by hbm2java
 */
public class AeUid implements java.io.Serializable {

	private AeUidId id;
	private String password;
	private char remote;
	private char userType;

	public AeUid() {
	}

	public AeUid(AeUidId id, String password, char remote, char userType) {
		this.id = id;
		this.password = password;
		this.remote = remote;
		this.userType = userType;
	}

	public AeUidId getId() {
		return this.id;
	}

	public void setId(AeUidId id) {
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

}
