package com.scholastic.sbam.server.util;

import java.util.HashMap;

public class SnapshotMappings {
	
	public static String [] [] PARAMETER_PAIRS = {
			{"startDate",		"AGREEMENT_TERM.START_DATE"},
			{"endDate",			"AGREEMENT_TERM.END_DATE"},
			{"terminateDate",	"AGREEMENT_TERM.TERMINATE_DATE"}
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
