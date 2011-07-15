package com.scholastic.sbam.server.database.codegen;

// Generated Jul 14, 2011 10:05:06 PM by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * SnapshotParameter generated by hbm2java
 */
public class SnapshotParameter implements java.io.Serializable {

	private SnapshotParameterId id;
	private String parameterSource;
	private String parameterGroup;
	private int parameterType;
	private Integer intToValue;
	private Integer intFromValue;
	private String strFromValue;
	private String strToValue;
	private Date dateFromValue;
	private Date dateToValue;
	private Double dblFromValue;
	private Double dblToValue;

	public SnapshotParameter() {
	}

	public SnapshotParameter(SnapshotParameterId id, String parameterSource,
			String parameterGroup, int parameterType) {
		this.id = id;
		this.parameterSource = parameterSource;
		this.parameterGroup = parameterGroup;
		this.parameterType = parameterType;
	}

	public SnapshotParameter(SnapshotParameterId id, String parameterSource,
			String parameterGroup, int parameterType, Integer intToValue,
			Integer intFromValue, String strFromValue, String strToValue,
			Date dateFromValue, Date dateToValue, Double dblFromValue,
			Double dblToValue) {
		this.id = id;
		this.parameterSource = parameterSource;
		this.parameterGroup = parameterGroup;
		this.parameterType = parameterType;
		this.intToValue = intToValue;
		this.intFromValue = intFromValue;
		this.strFromValue = strFromValue;
		this.strToValue = strToValue;
		this.dateFromValue = dateFromValue;
		this.dateToValue = dateToValue;
		this.dblFromValue = dblFromValue;
		this.dblToValue = dblToValue;
	}

	public SnapshotParameterId getId() {
		return this.id;
	}

	public void setId(SnapshotParameterId id) {
		this.id = id;
	}

	public String getParameterSource() {
		return this.parameterSource;
	}

	public void setParameterSource(String parameterSource) {
		this.parameterSource = parameterSource;
	}

	public String getParameterGroup() {
		return this.parameterGroup;
	}

	public void setParameterGroup(String parameterGroup) {
		this.parameterGroup = parameterGroup;
	}

	public int getParameterType() {
		return this.parameterType;
	}

	public void setParameterType(int parameterType) {
		this.parameterType = parameterType;
	}

	public Integer getIntToValue() {
		return this.intToValue;
	}

	public void setIntToValue(Integer intToValue) {
		this.intToValue = intToValue;
	}

	public Integer getIntFromValue() {
		return this.intFromValue;
	}

	public void setIntFromValue(Integer intFromValue) {
		this.intFromValue = intFromValue;
	}

	public String getStrFromValue() {
		return this.strFromValue;
	}

	public void setStrFromValue(String strFromValue) {
		this.strFromValue = strFromValue;
	}

	public String getStrToValue() {
		return this.strToValue;
	}

	public void setStrToValue(String strToValue) {
		this.strToValue = strToValue;
	}

	public Date getDateFromValue() {
		return this.dateFromValue;
	}

	public void setDateFromValue(Date dateFromValue) {
		this.dateFromValue = dateFromValue;
	}

	public Date getDateToValue() {
		return this.dateToValue;
	}

	public void setDateToValue(Date dateToValue) {
		this.dateToValue = dateToValue;
	}

	public Double getDblFromValue() {
		return this.dblFromValue;
	}

	public void setDblFromValue(Double dblFromValue) {
		this.dblFromValue = dblFromValue;
	}

	public Double getDblToValue() {
		return this.dblToValue;
	}

	public void setDblToValue(Double dblToValue) {
		this.dblToValue = dblToValue;
	}

}
