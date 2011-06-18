package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.StatsAdmin;
import com.scholastic.sbam.server.database.util.HibernateAccessor;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbStatsAdmin extends HibernateAccessor {
	
	static String objectName = StatsAdmin.class.getSimpleName();
	
	public static StatsAdmin getById(int ucn) {
		return (StatsAdmin) getByField(objectName, "ucn", ucn, "status");
	}
	
	public static List<StatsAdmin> findAll() {
		List<Object> results = findAll(objectName);
		List<StatsAdmin> statsAdmin = new ArrayList<StatsAdmin>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				statsAdmin.add((StatsAdmin) results.get(i));
		return statsAdmin;
	}
}
