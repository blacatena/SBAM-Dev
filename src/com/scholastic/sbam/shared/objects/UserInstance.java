package com.scholastic.sbam.shared.objects;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserInstance extends BetterRowEditInstance implements IsSerializable {
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
	private char status;
	private String roleGroupTitle;
	
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
	public char getStatus() {
		return status;
	}
	public void setStatus(char status) {
		this.status = status;
	}
	@Override
	public void markForDeletion() {
		setStatus('X');
	}
	@Override
	public boolean thisIsDeleted() {
		return status == 'X';
	}
	@Override
	public boolean thisIsNewRecord() {
		return id == null;
	}
	@Override
	public boolean thisIsValid() {
		return true;
	}
	@Override
	public String returnTriggerProperty() {
		return "junk";
	}
	@Override
	public String returnTriggerValue() {
		return "junk";
	}
	public String getRoleGroupTitle() {
		return roleGroupTitle;
	}
	public void setRoleGroupTitle(String roleGroupTitle) {
		this.roleGroupTitle = roleGroupTitle;
	}
	public String toString() {
		return id +"/"+userName+"/"+firstName+"/"+lastName+"/"+createdDatetime+"/"+status+"/"+roleGroupTitle;
	}
}
