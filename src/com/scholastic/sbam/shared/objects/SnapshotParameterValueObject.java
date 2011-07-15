package com.scholastic.sbam.shared.objects;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class SnapshotParameterValueObject implements IsSerializable, BeanModelTag {
	
	public static int	INTEGER = 0;
	public static int	DOUBLE  = 1;
	public static int	STRING  = 2;
	public static int	DATE    = 3;
	public static int	BOOLEAN = 4;
	
	protected	int		valueType;
	
	protected	int		intValue;
	protected	double	doubleValue;
	protected	String	stringValue;
	protected	Date	dateValue;

	protected	SnapshotParameterValueObject	toValue;
	
	public SnapshotParameterValueObject() {
		
	}
	
	public SnapshotParameterValueObject(Object value) {
		setValue(value);
	}
	
	public SnapshotParameterValueObject(Object value, Object toValue) {
		setValue(value);
		setToValue(toValue);
	}
	
	public SnapshotParameterValueObject(String value) {
		stringValue = value;
		valueType = STRING;
	}
	
	public SnapshotParameterValueObject(int value) {
		intValue = value;
		valueType = INTEGER;
	}
	
	public SnapshotParameterValueObject(double value) {
		doubleValue = value;
		valueType = DOUBLE;
	}
	
	public SnapshotParameterValueObject(Date value) {
		dateValue = value;
		valueType = DATE;
	}
	
	public SnapshotParameterValueObject(boolean value) {
		intValue = value ? 1 : 0;
		valueType = BOOLEAN;
	}
	
	public SnapshotParameterValueObject(String value, String toValue) {
		stringValue = value;
		valueType = STRING;
		this.toValue = new SnapshotParameterValueObject(toValue);
	}
	
	public SnapshotParameterValueObject(int value, int toValue) {
		intValue = value;
		valueType = INTEGER;
		this.toValue = new SnapshotParameterValueObject(toValue);
	}
	
	public SnapshotParameterValueObject(double value, double toValue) {
		doubleValue = value;
		valueType = DOUBLE;
		this.toValue = new SnapshotParameterValueObject(toValue);
	}
	
	public SnapshotParameterValueObject(Date value, Date toValue) {
		dateValue = value;
		valueType = DATE;
		this.toValue = new SnapshotParameterValueObject(toValue);
	}
	
	public int getValueType() {
		return valueType;
	}

	public void setValueType(int valueType) {
		this.valueType = valueType;
	}
	
	public boolean getBooleanValue() {
		return intValue > 0;
	}
	
	public boolean isTrue() {
		return intValue > 0;
	}

	public int getIntValue() {
		return intValue;
	}
	
	public double getDoubleValue() {
		return doubleValue;
	}
	
	public String getStringValue() {
		return stringValue;
	}
	
	public Date getDateValue() {
		return dateValue;
	}
	
	public void setValue(Object value) {
		if (value instanceof Integer)
			setValue((Integer) value);
		else if (value instanceof Double)
			setValue((Double) value);
		else if (value instanceof Date)
			setValue((Date) value);
		else if (value instanceof String)
			setValue((String) value);
		else if (value instanceof Boolean)
			setValue((Boolean) value);
		else
			setValue(value.toString());
	}
	
	public void setValue(int value) {
		this.intValue = value;
		valueType = INTEGER;
	}
	
	public void setValue(double value) {
		this.doubleValue = value;
		valueType = DOUBLE;
	}
	
	public void setValue(String value) {
		this.stringValue = value;
		valueType = STRING;
	}
	
	public void setValue(Date value) {
		this.dateValue = value;
		valueType = DATE;
	}
	
	public void setValue(Boolean value) {
		this.intValue = value ? 1 : 0;
		valueType = BOOLEAN;
	}
	
	public SnapshotParameterValueObject getToValue() {
		return toValue;
	}
	
	public void setToValue(SnapshotParameterValueObject toValue) {
		this.toValue = toValue;
	}
	
	public boolean isInteger() {
		return valueType == INTEGER;
	}
	
	public boolean isDouble() {
		return valueType == DOUBLE;
	}
	
	public boolean isDate() {
		return valueType == DATE;
	}
	
	public boolean isString() {
		return valueType == STRING;
	}
	
	public boolean isBoolean() {
		return valueType == BOOLEAN;
	}

	public boolean isRange() {
		return toValue != null;
	}
	
	public int getToIntValue() {
		return toValue.intValue;
	}
	
	public double getToDoubleValue() {
		return toValue.doubleValue;
	}
	
	public String getToStringValue() {
		return toValue.stringValue;
	}
	
	public Date getToDateValue() {
		return toValue.dateValue;
	}
	
	public void setToValue(Object value) {
		toValue = new SnapshotParameterValueObject(value);
	}
	
	public void setToValue(int value) {
		toValue = new SnapshotParameterValueObject(value);
	}
	
	public void setToValue(double value) {
		toValue = new SnapshotParameterValueObject(value);
	}
	
	public void setToValue(String value) {
		toValue = new SnapshotParameterValueObject(value);
	}
	
	public void setToValue(Date value) {
		toValue = new SnapshotParameterValueObject(value);
	}
	
	public String toString() {
		if (toValue == null) {
			if (isInteger())
				return "" + intValue;
			else if (isDouble())
				return "" + doubleValue;
			else if (isDate())
				return "" + dateValue;
			else if (isString())
				return stringValue;
			else if (isBoolean())
				return intValue > 0 ? "true" : "false";
			else
				return "null";
		} else {
			if (isInteger())
				return intValue + " <=> " + toValue.toString();
			else if (isDouble())
				return doubleValue + " <=> " + toValue.toString();
			else if (isDate())
				return dateValue + " <=> " + toValue.toString();
			else if (isString())
				return stringValue + " <=> " + toValue.toString();
			else if (isBoolean())
				return intValue > 0 ? "true" : "false";
			else
				return "null";
		}
	}
}
