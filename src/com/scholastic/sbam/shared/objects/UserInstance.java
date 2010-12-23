package com.scholastic.sbam.shared.objects;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserInstance implements IsSerializable {
	private Integer id;
	private String userName;
	private String password;
	private String firstName;
	private String lastName;
	private String email;
	private int loginCount;
	private int sessionId;
	private Date sessionStartTime;
	private Date sessionExpireTime;
	private Date createdDatetime;
	private String status;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getLoginCount() {
		return loginCount;
	}
	public void setLoginCount(int loginCount) {
		this.loginCount = loginCount;
	}
	public int getSessionId() {
		return sessionId;
	}
	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}
	public Date getSessionStartTime() {
		return sessionStartTime;
	}
	public void setSessionStartTime(Date sessionStartTime) {
		this.sessionStartTime = sessionStartTime;
	}
	public Date getSessionExpireTime() {
		return sessionExpireTime;
	}
	public void setSessionExpireTime(Date sessionExpireTime) {
		this.sessionExpireTime = sessionExpireTime;
	}
	public Date getCreatedDatetime() {
		return createdDatetime;
	}
	public void setCreatedDatetime(Date createdDatetime) {
		this.createdDatetime = createdDatetime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setStatus(char status) {
		this.status = status + "";
	}
	
}
