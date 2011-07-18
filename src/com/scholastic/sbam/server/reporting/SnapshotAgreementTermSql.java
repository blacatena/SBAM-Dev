package com.scholastic.sbam.server.reporting;

import java.text.SimpleDateFormat;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.Snapshot;
import com.scholastic.sbam.server.database.codegen.SnapshotProductService;
import com.scholastic.sbam.server.util.SnapshotMappings;
import com.scholastic.sbam.shared.objects.SnapshotInstance;
import com.scholastic.sbam.shared.objects.SnapshotParameterSetInstance;
import com.scholastic.sbam.shared.objects.SnapshotParameterValueObject;
import com.scholastic.sbam.shared.util.AppConstants;

public class SnapshotAgreementTermSql {
	
	protected SimpleDateFormat				sqlDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	
	protected Snapshot						dbSnapshot;
	
	protected SnapshotParameterSetInstance	parameters;
	
	protected List<SnapshotProductService>	snapshotProductServices;
	
	protected StringBuffer					sb;
	
	public SnapshotAgreementTermSql(Snapshot dbSnapshot, SnapshotParameterSetInstance parameters, List<SnapshotProductService> snapshotProductServices) {
		this.dbSnapshot = dbSnapshot;
		this.parameters = parameters;
		this.snapshotProductServices = snapshotProductServices;
	}
	
	public String getSnapshotSql() {
		sb = new StringBuffer();
		
		appendSelect();
		sb.append(" ");
		appendFrom();
		sb.append(" ");
		appendStandardWhere();
		sb.append(" ");
		appendSnapshotConditions();
		
		return sb.toString();
	}
	
	protected void appendSelect() {
		sb.append("select");
		sb.append(" ");
		sb.append(dbSnapshot.getSnapshotId());
		sb.append(" as SNAPSHOT_ID");
		sb.append(",");
		sb.append("AGREEMENT_TERM.AGREEMENT_ID as AGREEMENT_ID");
		sb.append(",");
		sb.append("AGREEMENT_TERM.TERM_ID as TERM_ID");
		sb.append(",");
		sb.append("AGREEMENT_TERM.PRODUCT_CODE as PRODUCT_CODE");
		sb.append(",");
		if (dbSnapshot.getProductServiceType() == SnapshotInstance.SERVICE_TYPE) {
			sb.append("PRODUCT_SERVICE.SERVICE_CODE as SERVICE_CODE");
		} else {
			sb.append("'' as SERVICE_CODE");
		}
		sb.append(",");
		sb.append("AGREEMENT_TERM.START_DATE as START_DATE");
		sb.append(",");
		sb.append("AGREEMENT_TERM.END_DATE as END_DATE");
		sb.append(",");
		sb.append("AGREEMENT_TERM.TERMINATE_DATE as TERMINATE_DATE");
		sb.append(",");
		sb.append("AGREEMENT_TERM.TERM_TYPE as TERM_TYPE");
		sb.append(",");
		sb.append("AGREEMENT_TERM.CANCEL_REASON_CODE as CANCEL_REASON_CODE");
		sb.append(",");
		sb.append("AGREEMENT_TERM.CANCEL_DATE as CANCEL_DATE");
		sb.append(",");
		sb.append("AGREEMENT_TERM.DOLLAR_VALUE as DOLLAR_VALUE");
		sb.append(",");
		sb.append("AGREEMENT_TERM.WORKSTATIONS as WORKSTATIONS");
		sb.append(",");
		sb.append("AGREEMENT_TERM.BUILDINGS as BUILDINGS");
		sb.append(",");
		sb.append("AGREEMENT_TERM.POPULATION as POPULATION");
		sb.append(",");
		sb.append("AGREEMENT_TERM.ENROLLMENT as ENROLLMENT");
		sb.append(",");
		sb.append("AGREEMENT_TERM.COMMISSION_CODE as COMMISSION_CODE");
		sb.append(",");
		sb.append("AGREEMENT_TERM.PRIMARY_TERM as PRIMARY_TERM");	
	}
	
	protected void appendFrom() {
		sb.append("FROM");
		sb.append(" ");
		sb.append("AGREEMENT, AGREEMENT_TERM, PRODUCT");
		if (dbSnapshot.getProductServiceType() == SnapshotInstance.SERVICE_TYPE) {
			sb.append(",PRODUCT_SERVICE,SERVICE");
		}
	}
	
	protected void appendStandardWhere() {
		sb.append("WHERE");
		
		sb.append(" ");
		sb.append("AGREEMENT.ID = AGREEMENT_TERM.AGREEMENT_ID");
		
		sb.append(" ");
		sb.append("AND");
		sb.append(" ");
		sb.append("AGREEMENT.STATUS <> ");
		sb.append("'");
		sb.append(AppConstants.STATUS_DELETED);
		sb.append("'");
		
		sb.append(" ");
		sb.append("AND");
		sb.append(" ");
		sb.append("AGREEMENT_TERM.PRODUCT_CODE = PRODUCT.PRODUCT_CODE");
		
		sb.append(" ");
		sb.append("AND");
		sb.append(" ");
		sb.append("PRODUCT.STATUS = ");
		sb.append("'");
		sb.append(AppConstants.STATUS_ACTIVE);
		sb.append("'");
		
		if (dbSnapshot.getProductServiceType() == SnapshotInstance.SERVICE_TYPE) {
			
			sb.append(" ");
			sb.append("AND");
			sb.append(" ");
			sb.append("AGREEMENT_TERM.PRODUCT_CODE = PRODUCT_SERVICE.PRODUCT_CODE");
			
			sb.append(" ");
			sb.append("AND");
			sb.append(" ");
			sb.append("PRODUCT_SERVICE.SERVICE_CODE = SERVICE.SERVICE_CODE");
			
			sb.append(" ");
			sb.append("AND");
			sb.append(" ");
			sb.append("PRODUCT_SERVICE.STATUS = ");
			sb.append("'");
			sb.append(AppConstants.STATUS_ACTIVE);
			sb.append("'");
			
			sb.append(" ");
			sb.append("AND");
			sb.append(" ");
			sb.append("SERVICE.STATUS = ");
			sb.append("'");
			sb.append(AppConstants.STATUS_ACTIVE);
			sb.append("'");
		}
	}
	
	protected void appendSnapshotConditions() {
		appendParameterConditions(sb, dbSnapshot, parameters);
		appendProductServiceRestrictions();
	}
		
	protected void appendParameterConditions(StringBuffer sb, Snapshot dbSnapshot, SnapshotParameterSetInstance parameters) {
			
		for (String parameterName : parameters.getValues().keySet()) {
			if (parameterName.equals(""))
				continue;
			List<SnapshotParameterValueObject> values = parameters.getValues(parameterName);
			if (values.size() == 0)
				continue;
			
			sb.append(" ");
			sb.append("AND");
			sb.append(" ");
			
			if (values.size() == 1)
				addSingleValueCondition(parameterName, values.get(0));
			else
				addMultipleValueCondition(parameterName, values);
		}
	}
		
	protected void appendProductServiceRestrictions() {
		if (snapshotProductServices == null || snapshotProductServices.size() == 0)
			return;
		
		sb.append(" ");
		sb.append("AND");
		sb.append(" ");
		if (dbSnapshot.getProductServiceType() == SnapshotInstance.PRODUCT_TYPE) {
			sb.append("PRODUCT.PRODUCT_CODE");
		} else if (dbSnapshot.getProductServiceType() == SnapshotInstance.SERVICE_TYPE) {
			sb.append("SERVICE.SERVICE_CODE");
		} else
			throw new IllegalArgumentException("INTERNAL ERROR: Invalid snapshot product/service type " + dbSnapshot.getProductServiceType() + ".");
		
		sb.append(" ");
		sb.append("IN (");
		
		int count = 0;
		for (SnapshotProductService productService : snapshotProductServices) {
			if (count > 0)
				sb.append(", ");
			sb.append("'");
			sb.append(productService.getId().getProductServiceCode());
			sb.append("'");
			count++;
		}
		
		sb.append(")");
	}
	
	protected void addMultipleValueCondition(String parameterName, List<SnapshotParameterValueObject> values) {
		sb.append(" ");
		sb.append(SnapshotMappings.getParameterSqlMapping(parameterName));
		sb.append(" ");
		sb.append("IN (");
		int count = 0;
		for (SnapshotParameterValueObject value : values) {
			if (count > 0)
				sb.append(", ");
			if (value.isRange())
				throw new IllegalArgumentException("Attempt to apply a range snapshot parameter within a value list.");
			addSqlValue(value);
			count++;
		}
		sb.append(")");
	}

	protected void addSingleValueCondition(String parameterName, SnapshotParameterValueObject value) {
		if (value.isRange())
			addRangeCondition(parameterName, value);
		else {
			sb.append(" ");
			sb.append(SnapshotMappings.getParameterSqlMapping(parameterName));
			sb.append(" = ");
			addSqlValue(value);
		}
	}
	
	protected void addRangeCondition(String parameterName, SnapshotParameterValueObject value) {
		sb.append(" ");
		sb.append(SnapshotMappings.getParameterSqlMapping(parameterName));
		sb.append(" ");
		sb.append("BETWEEN");
		sb.append(" ");
		addSqlValue(value);
		sb.append(" ");
		sb.append("AND");
		sb.append(" ");
		addSqlValue(value.getToValue());
	}
	
	protected void addSqlValue(SnapshotParameterValueObject value) {
		if (value.isInteger()) {
			sb.append(value.getIntValue());
		} else if (value.isDouble()) {
			sb.append(value.getDoubleValue());
		} else if (value.isString()) {
			sb.append("'");
			sb.append(value.getStringValue().replace("'", "''"));
			sb.append("'");
		} else if (value.isDate()) {
			sb.append("'");
			sb.append(sqlDateFormatter.format(value.getDateValue()));
			sb.append("'");
		} else if (value.isBoolean()) {
			throw new IllegalArgumentException("Attempt to apply a boolean snapshot parameter dirctly to the SQL.");
		} else
			throw new IllegalArgumentException("Invalid parameter type " + value.getValueType());
	}
}
