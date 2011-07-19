package com.scholastic.sbam.server.database.codegen;

// Generated Jul 19, 2011 10:00:16 AM by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * WelcomeMessage generated by hbm2java
 */
public class WelcomeMessage implements java.io.Serializable {

	private Integer id;
	private Date postDate;
	private String title;
	private String content;
	private char priority;
	private Date expireDate;
	private char status;

	public WelcomeMessage() {
	}

	public WelcomeMessage(Date postDate, String title, String content,
			char priority, Date expireDate, char status) {
		this.postDate = postDate;
		this.title = title;
		this.content = content;
		this.priority = priority;
		this.expireDate = expireDate;
		this.status = status;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getPostDate() {
		return this.postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public char getPriority() {
		return this.priority;
	}

	public void setPriority(char priority) {
		this.priority = priority;
	}

	public Date getExpireDate() {
		return this.expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public char getStatus() {
		return this.status;
	}

	public void setStatus(char status) {
		this.status = status;
	}

}
