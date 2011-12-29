package com.scholastic.sbam.server.reporting;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.Snapshot;
import com.scholastic.sbam.server.database.codegen.SnapshotProductService;
import com.scholastic.sbam.shared.objects.SnapshotInstance;
import com.scholastic.sbam.shared.objects.SnapshotParameterSetInstance;
import com.scholastic.sbam.shared.objects.SnapshotParameterValueObject;
import com.scholastic.sbam.shared.util.AppConstants;

public class SnapshotAgreementTermSql {
	
	protected SimpleDateFormat				sqlDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	
	protected Snapshot						dbSnapshot;
	
	protected SnapshotParameterSetInstance	parameters;
	
	protected List<SnapshotProductService>	snapshotProducts;
	
	protected List<SnapshotProductService>	snapshotServices;
	
	protected HashMap<String, SnapshotProductServiceEntry>	productServiceMap;
	
	protected StringBuffer					sb;
	
	public SnapshotAgreementTermSql(	Snapshot dbSnapshot, 
										SnapshotParameterSetInstance parameters, 
										List<SnapshotProductService> snapshotProducts, 
										List<SnapshotProductService> snapshotServices, 
										HashMap<String, SnapshotProductServiceEntry>	productServiceMap) {
		this.dbSnapshot = dbSnapshot;
		this.parameters = parameters;
		this.snapshotProducts = snapshotProducts;
		this.snapshotServices = snapshotServices;
		this.productServiceMap = productServiceMap;
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
		sb.append("select distinct ");
		sb.append(" ");
		sb.append(dbSnapshot.getSnapshotId());
		sb.append(" as SNAPSHOT_ID");
		sb.append(",");
		sb.append("agreement_term.AGREEMENT_ID as AGREEMENT_ID");
		sb.append(",");
		sb.append("agreement_term.TERM_ID as TERM_ID");
		sb.append(",");
		sb.append("agreement_term.PRODUCT_CODE as PRODUCT_CODE");
		sb.append(",");
		if (dbSnapshot.getProductServiceType() == SnapshotInstance.SERVICE_TYPE) {
			sb.append("product_service.SERVICE_CODE as SERVICE_CODE");
		} else {
			sb.append("'' as SERVICE_CODE");
		}
		sb.append(",");
		sb.append("agreement_term.START_DATE as START_DATE");
		sb.append(",");
		sb.append("agreement_term.END_DATE as END_DATE");
		sb.append(",");
		sb.append("agreement_term.TERMINATE_DATE as TERMINATE_DATE");
		sb.append(",");
		sb.append("agreement_term.TERM_TYPE as TERM_TYPE");
		sb.append(",");
		sb.append("agreement_term.CANCEL_REASON_CODE as CANCEL_REASON_CODE");
		sb.append(",");
		sb.append("agreement_term.CANCEL_DATE as CANCEL_DATE");
		sb.append(",");
		sb.append("agreement_term.DOLLAR_VALUE as DOLLAR_VALUE");
		sb.append(",");
		sb.append("agreement_term.WORKSTATIONS as WORKSTATIONS");
		sb.append(",");
		sb.append("agreement_term.BUILDINGS as BUILDINGS");
		sb.append(",");
		sb.append("agreement_term.POPULATION as POPULATION");
		sb.append(",");
		sb.append("agreement_term.ENROLLMENT as ENROLLMENT");
		sb.append(",");
		sb.append("agreement_term.COMMISSION_CODE as COMMISSION_CODE");
		sb.append(",");
		sb.append("agreement_term.PRIMARY_TERM as PRIMARY_TERM");	
	}
	
	protected void appendFrom() {
		sb.append("FROM");
		sb.append(" ");
		sb.append("agreement, agreement_term, product");
		if (dbSnapshot.getProductServiceType() == SnapshotInstance.SERVICE_TYPE) {
			sb.append(",product_service,service");
		}
	}
	
	protected void appendStandardWhere() {
		sb.append("WHERE");
		
		sb.append(" ");
		sb.append("agreement.ID = agreement_term.AGREEMENT_ID");
		
		sb.append(" ");
		sb.append("AND");
		sb.append(" ");
		sb.append("agreement.STATUS <> ");
		sb.append("'");
		sb.append(AppConstants.STATUS_DELETED);
		sb.append("'");
		
		sb.append(" ");
		sb.append("AND");
		sb.append(" ");
		sb.append("agreement_term.PRODUCT_CODE = product.PRODUCT_CODE");
		
		sb.append(" ");
		sb.append("AND");
		sb.append(" ");
		sb.append("product.STATUS = ");
		sb.append("'");
		sb.append(AppConstants.STATUS_ACTIVE);
		sb.append("'");
		
		if (dbSnapshot.getProductServiceType() == SnapshotInstance.SERVICE_TYPE) {
			
			sb.append(" ");
			sb.append("AND");
			sb.append(" ");
			sb.append("agreement_term.PRODUCT_CODE = product_service.PRODUCT_CODE");
			
			sb.append(" ");
			sb.append("AND");
			sb.append(" ");
			sb.append("product_service.SERVICE_CODE = service.SERVICE_CODE");
			
			sb.append(" ");
			sb.append("AND");
			sb.append(" ");
			sb.append("product_service.STATUS = ");
			sb.append("'");
			sb.append(AppConstants.STATUS_ACTIVE);
			sb.append("'");
			
			sb.append(" ");
			sb.append("AND");
			sb.append(" ");
			sb.append("service.STATUS = ");
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
			
			String columnName = SnapshotMappings.getParameterSqlMapping(parameterName);
					
			if (columnName == null)	//	This parameter does not add a SQL condition this way
				continue;
			
			sb.append(" ");
			sb.append("AND");
			sb.append(" ");
			
			if (values.size() == 1)
				addSingleValueCondition(columnName, values.get(0));
			else
				addMultipleValueCondition(columnName, values);
		}
	}
	
	protected void appendProductServiceRestrictions() {
		appendProductServiceRestrictions(SnapshotInstance.PRODUCT_TYPE, snapshotProducts);
		appendProductServiceRestrictions(SnapshotInstance.SERVICE_TYPE, snapshotServices);
	}
		
	protected void appendProductServiceRestrictions(char productServiceType, List<SnapshotProductService> snapshotProductServices) {
		if (snapshotProductServices == null || snapshotProductServices.size() == 0)
			return;
		
		sb.append(" ");
		sb.append("AND");
		sb.append(" ");
		
		if (dbSnapshot.getProductServiceType() == productServiceType || productServiceType == SnapshotInstance.PRODUCT_TYPE) {
			//	By product, or if both report and select by service, then just add an in clause
			if (productServiceType == SnapshotInstance.PRODUCT_TYPE) {
				sb.append("product.PRODUCT_CODE");
			} else if (productServiceType == SnapshotInstance.SERVICE_TYPE) {
				sb.append("service.SERVICE_CODE");
			} else
				throw new IllegalArgumentException("INTERNAL ERROR: Invalid snapshot product/service type " + dbSnapshot.getProductServiceType() + ".");
			appendProductServiceInClause(snapshotProductServices);
		} else {
			//	If reporting by product and constraining by service, build an in-clause of products based on service components
			sb.append("product.PRODUCT_CODE");
			appendProductInClause(snapshotProductServices);
		}
	}
	
	protected void appendProductServiceInClause(List<SnapshotProductService> snapshotProductServices) {
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
	
	protected void appendProductInClause(List<SnapshotProductService> snapshotProductServices) {
		sb.append(" ");
		sb.append("IN (");
		
		int count = 0;
		for (SnapshotProductServiceEntry productServiceEntry : productServiceMap.values()) {
			
			//	See if this product has any of the requested services
			int serviceCount = 0;
			for (SnapshotProductService snapshotProductService : snapshotProductServices) {
				if (productServiceEntry.hasService(snapshotProductService.getId().getProductServiceCode())) {
					serviceCount++;
					break;	// No need to look for more... on eis enough
				}
			}
			
			//	No services, skip this product
			if (serviceCount == 0)
				continue;
			
			if (count > 0)
				sb.append(", ");
			sb.append("'");
			sb.append(productServiceEntry.getProduct().getProductCode());
			sb.append("'");
			count++;
		}
		
		sb.append(")");
	}
	
	protected void addMultipleValueCondition(String columnName, List<SnapshotParameterValueObject> values) {
		sb.append(" ");
		sb.append(columnName);
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

	protected void addSingleValueCondition(String columnName, SnapshotParameterValueObject value) {
		if (value.isRange())
			addRangeCondition(columnName, value);
		else {
			sb.append(" ");
			sb.append(columnName);
			sb.append(" = ");
			addSqlValue(value);
		}
	}
	
	protected void addRangeCondition(String columnName, SnapshotParameterValueObject value) {
		sb.append(" ");
		sb.append(columnName);
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
