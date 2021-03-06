package com.scholastic.sbam.server.database.codegen;

// Generated Feb 1, 2012 5:33:22 PM by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * Snapshot generated by hbm2java
 */
public class Snapshot implements java.io.Serializable {

	private Integer snapshotId;
	private String snapshotName;
	private String snapshotType;
	private char productServiceType;
	private char ucnType;
	private Date snapshotTaken;
	private int snapshotRows;
	private String excelFilename;
	private int createUserId;
	private Date expireDatetime;
	private int seq;
	private String orgPath;
	private String note;
	private Date createdDatetime;
	private char status;

	public Snapshot() {
	}

	public Snapshot(String snapshotName, String snapshotType,
			char productServiceType, char ucnType, int snapshotRows,
			int createUserId, int seq, String orgPath, String note,
			Date createdDatetime, char status) {
		this.snapshotName = snapshotName;
		this.snapshotType = snapshotType;
		this.productServiceType = productServiceType;
		this.ucnType = ucnType;
		this.snapshotRows = snapshotRows;
		this.createUserId = createUserId;
		this.seq = seq;
		this.orgPath = orgPath;
		this.note = note;
		this.createdDatetime = createdDatetime;
		this.status = status;
	}

	public Snapshot(String snapshotName, String snapshotType,
			char productServiceType, char ucnType, Date snapshotTaken,
			int snapshotRows, String excelFilename, int createUserId,
			Date expireDatetime, int seq, String orgPath, String note,
			Date createdDatetime, char status) {
		this.snapshotName = snapshotName;
		this.snapshotType = snapshotType;
		this.productServiceType = productServiceType;
		this.ucnType = ucnType;
		this.snapshotTaken = snapshotTaken;
		this.snapshotRows = snapshotRows;
		this.excelFilename = excelFilename;
		this.createUserId = createUserId;
		this.expireDatetime = expireDatetime;
		this.seq = seq;
		this.orgPath = orgPath;
		this.note = note;
		this.createdDatetime = createdDatetime;
		this.status = status;
	}

	public Integer getSnapshotId() {
		return this.snapshotId;
	}

	public void setSnapshotId(Integer snapshotId) {
		this.snapshotId = snapshotId;
	}

	public String getSnapshotName() {
		return this.snapshotName;
	}

	public void setSnapshotName(String snapshotName) {
		this.snapshotName = snapshotName;
	}

	public String getSnapshotType() {
		return this.snapshotType;
	}

	public void setSnapshotType(String snapshotType) {
		this.snapshotType = snapshotType;
	}

	public char getProductServiceType() {
		return this.productServiceType;
	}

	public void setProductServiceType(char productServiceType) {
		this.productServiceType = productServiceType;
	}

	public char getUcnType() {
		return this.ucnType;
	}

	public void setUcnType(char ucnType) {
		this.ucnType = ucnType;
	}

	public Date getSnapshotTaken() {
		return this.snapshotTaken;
	}

	public void setSnapshotTaken(Date snapshotTaken) {
		this.snapshotTaken = snapshotTaken;
	}

	public int getSnapshotRows() {
		return this.snapshotRows;
	}

	public void setSnapshotRows(int snapshotRows) {
		this.snapshotRows = snapshotRows;
	}

	public String getExcelFilename() {
		return this.excelFilename;
	}

	public void setExcelFilename(String excelFilename) {
		this.excelFilename = excelFilename;
	}

	public int getCreateUserId() {
		return this.createUserId;
	}

	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}

	public Date getExpireDatetime() {
		return this.expireDatetime;
	}

	public void setExpireDatetime(Date expireDatetime) {
		this.expireDatetime = expireDatetime;
	}

	public int getSeq() {
		return this.seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getOrgPath() {
		return this.orgPath;
	}

	public void setOrgPath(String orgPath) {
		this.orgPath = orgPath;
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

	public char getStatus() {
		return this.status;
	}

	public void setStatus(char status) {
		this.status = status;
	}

}
