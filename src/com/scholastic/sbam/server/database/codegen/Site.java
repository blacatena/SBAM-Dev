package com.scholastic.sbam.server.database.codegen;

// Generated Jan 14, 2011 6:28:15 PM by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * Site generated by hbm2java
 */
public class Site implements java.io.Serializable {

	private SiteId id;
	private String description;
	private Date createdDatetime;
	private String status;

	public Site() {
	}

	public Site(SiteId id, String description, Date createdDatetime,
			String status) {
		this.id = id;
		this.description = description;
		this.createdDatetime = createdDatetime;
		this.status = status;
	}

	public SiteId getId() {
		return this.id;
	}

	public void setId(SiteId id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Date createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
