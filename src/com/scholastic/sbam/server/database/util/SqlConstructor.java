package com.scholastic.sbam.server.database.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SqlConstructor {
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	
	protected StringBuffer sql = new StringBuffer();
	protected boolean		whereStarted;
	
	public SqlConstructor() {
		
	}
	
	public void reset() {
		sql = new StringBuffer();
		whereStarted = false;
	}
	
	public void addAnd() {
		if (whereStarted)
			sql.append(" AND ");
		else {
			whereStarted = true;
			sql.append(" WHERE ");
		}
	}
	
	public void append(String moreSql) {
		if (moreSql.length() == 0)
			return;
		if (moreSql.charAt(0) != ' ')
			sql.append(" ");
		sql.append(moreSql);
	}
	
	public void addCondition(String condition) {
		addAnd();
		sql.append(condition);
	}
	
	public void addCondition(String condition, String value) {
		addAnd();
		sql.append(condition);
		sql.append("'");
		sql.append(value);
		sql.append("'");
	}
	
	public void addCondition(String condition, char value) {
		addAnd();
		sql.append(condition);
		sql.append("'");
		sql.append(value);
		sql.append("'");
	}
	
	public void addCondition(String condition, int value) {
		addAnd();
		sql.append(condition);
		sql.append(value);
	}
	
	public void addCondition(String condition, Date value) {
		addAnd();
		sql.append(condition);
		sql.append("'");
		sql.append(format.format(value));
		sql.append("'");
	}
	
	public String getSql() {
		return sql.toString();
	}
	
	public String toString() {
		return sql.toString();
	}
}
