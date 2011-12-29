package com.scholastic.sbam.server.reporting;

import java.util.HashMap;

public class SnapshotMappings {
	
	/**
	 * These are form variable names (mostly stored in the snapshot parameter table) and the corresponding database column.
	 * 
	 * If the database column is blank or null, then the variable is handled specially (such as the UCN or product/service type, which is stored
	 * directly in the snapshot table and affects select behavior rather than keying on a particular table/column).
	 */
	public static String [] [] PARAMETER_PAIRS = {
			{"startDate",			"agreement_term.START_DATE"},
			{"endDate",				"agreement_term.END_DATE"},
			{"terminateDate",		"agreement_term.TERMINATE_DATE"},
			{"termTypes",			"agreement_term.TERM_TYPE"},
			{"termCommCodes",		"agreement_term.COMMISSION_CODE"},
			{"productCommCodes",	"product.DEFAULT_COMMISSION_CODE"},
			{"agreementCommCodes",	"agreement.COMMISSION_CODE"},
			{"siteCommCodes",		"agreement_site.COMMISSION_CODE"},
			/* From here down are non-column-select variables */
			{"ucnType",				null},
			{"productServiceType",	null},
			{"stateCodes",			null},
			{"countryCodes",		null}
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
