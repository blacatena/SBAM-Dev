package com.scholastic.sbam.server.database.codegen;

// Generated Jun 3, 2011 6:43:10 PM by Hibernate Tools 3.2.4.GA

/**
 * UserRole generated by hbm2java
 */
public class UserRole implements java.io.Serializable {

	private UserRoleId id;
	private int readOrWrite;

	public UserRole() {
	}

	public UserRole(UserRoleId id, int readOrWrite) {
		this.id = id;
		this.readOrWrite = readOrWrite;
	}

	public UserRoleId getId() {
		return this.id;
	}

	public void setId(UserRoleId id) {
		this.id = id;
	}

	public int getReadOrWrite() {
		return this.readOrWrite;
	}

	public void setReadOrWrite(int readOrWrite) {
		this.readOrWrite = readOrWrite;
	}

}
