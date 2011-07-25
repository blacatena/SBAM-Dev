package com.scholastic.sbam.shared.reporting;

import java.util.HashMap;

public class SnapshotParameterNames {
	// Groups
	
	public final static String			DATES_GROUP					=	"Dates";
	
	// Individual Parameters
	
	public final static String			START_DATE					=	"startDate";
	public final static String			END_DATE					=	"endDate";
	public final static String			TERMINATE_DATE				=	"terminateDate";
	public final static String			UCN_TYPE					=	"ucnType";
	public final static String			PRODUCT_SERVICE_TYPE		=	"productServiceType";
	public final static String			TERM_TYPES					=	"termTypes";
	public final static String			PROD_COMM_CODES				=	"productCommCodes";
	public final static String			AGREEMENT_COMM_CODES		=	"agreementCommCodes";
	public final static String			TERM_COMM_CODES				=	"termCommCodes";
	
	public final static HashMap<String, String> labels				=	getLabelsMap();
	
	public static HashMap<String, String> getLabelsMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put(START_DATE, 			"Start Date");
		map.put(END_DATE,				"End Date");
		map.put(TERMINATE_DATE, 		"Terminate Date");
		map.put(UCN_TYPE,				"UCNs Type");
		map.put(PRODUCT_SERVICE_TYPE,	"Product Type");
		map.put(TERM_TYPES, 			"Term Types");
		map.put(PROD_COMM_CODES,		"Product Commission Code");
		map.put(AGREEMENT_COMM_CODES,	"Agreement Commission Code");
		map.put(TERM_COMM_CODES,		"Term Commission Code");
		
		return map;
	}
	
	public static String getName(String parameter) {
		if (labels.containsKey(parameter))
			return labels.get(parameter);
		return parameter;
	}
}
