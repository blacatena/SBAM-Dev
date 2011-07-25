package com.scholastic.sbam.server.util;

import java.util.HashMap;

public class SnapshotMappings {
	
	/**
	 * These are form variable names (mostly stored in the snapshot parameter table) and the corresponding database column.
	 * 
	 * If the database column is blank or null, then the variable is handled specially (such as the UCN or product/service type, which is stored
	 * directly in the snapshot table and affects select behavior rather than keying on a particular table/column).
	 */
	public static String [] [] PARAMETER_PAIRS = {
			{"startDate",			"AGREEMENT_TERM.START_DATE"},
			{"endDate",				"AGREEMENT_TERM.END_DATE"},
			{"terminateDate",		"AGREEMENT_TERM.TERMINATE_DATE"},
			{"termTypes",			"AGREEMENT_TERM.TERM_TYPE"},
			{"termCommCodes",		"AGREEMENT_TERM.COMMISSION_CODE"},
			{"productCommCodes",	"PRODUCT.DEFAULT_COMMISSION_CODE"},
			{"agreementCommCodes",	"AGREEMENT.COMMISSION_CODE"},
			{"siteCommCodes",		"AGREEMENT_SITE.COMMISSION_CODE"},
			/* From here down are non-column-select variables */
			{"ucnType",				null},
			{"productServiceType",	null}
	};
	
	public static HashMap<String, String> PARAMETER_MAPPINGS = getSnapshotMap();
	
	protected static HashMap<String, String> getSnapshotMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		for (String [] pair : PARAMETER_PAIRS) {
			map.put(pair [0], pair [1]);
		}
		return map;
	};
	
	public static String getParameterSqlMapping(String name) {
		if (PARAMETER_MAPPINGS.containsKey(name))
			return PARAMETER_MAPPINGS.get(name);
		throw new IllegalArgumentException("Missing SnapshotMappings mapping for parameter name " + name + ".");
	}
}
