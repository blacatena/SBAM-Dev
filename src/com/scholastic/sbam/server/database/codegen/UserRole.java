package com.scholastic.sbam.server.database.codegen;

// Generated Dec 2, 2010 1:42:47 PM by Hibernate Tools 3.2.4.GA

/**
 * UserRole generated by hbm2java
 */
public class UserRole implements java.io.Serializable {

	private UserRoleId id;
	private int readWrite;

	public UserRole() {
	}

	public UserRole(UserRoleId id, int readWrite) {
		this.id = id;
		this.readWrite = readWrite;
	}

	public UserRoleId getId() {
		return this.id;
	}

	public void setId(UserRoleId id) {
		this.id = id;
	}

	public int getReadWrite() {
		return this.readWrite;
	}

	public void setReadWrite(int readWrite) {
		this.readWrite = readWrite;
	}

}
