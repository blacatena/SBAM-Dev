package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.StatsAdmin;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.StatsAdminInstance;

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
	
	public static StatsAdminInstance getInstance(StatsAdmin dbInstance) {
		StatsAdminInstance instance = StatsAdminInstance.getEmptyInstance();
		
		instance.setUcn(dbInstance.getUcn());
		instance.setAdminUid(dbInstance.getAdminUid());
		instance.setAdminPassword(dbInstance.getAdminPassword());
		instance.setStatsGroup(dbInstance.getStatsGroup());
		instance.setNote(dbInstance.getNote());
		instance.setStatus(dbInstance.getStatus());
		
		return instance;
	}
	
//	public static void setDescriptions(StatsAdminInstance statsAdmin, InstitutionInstance institution) {
//		if (statsAdmin == null)
//			return;
//		
//		if (statsAdmin.getUcn() > 0) {
//			if (institution != null && statsAdmin.getUcn() == institution.getUcn()) {
//				statsAdmin.setInstitution(institution);
//			} else {
//				Institution dbInstitution = DbInstitution.getByCode(statsAdmin.getUcn());
//				if (dbInstitution != null) {
//					statsAdmin.setInstitution( DbInstitution.getInstance(dbInstitution) );
//				} else
//					statsAdmin.setInstitution( InstitutionInstance.getUnknownInstance( statsAdmin.getUcn()) );
//			}
//		} else {
//			statsAdmin.setInstitution( InstitutionInstance.getEmptyInstance());
//		}
//		
//		try {
//			if (InstitutionCache.getSingleton() != null)
//				InstitutionCache.getSingleton().setDescriptions(statsAdmin.getInstitution());
//		} catch (InstitutionCacheConflict e) {
//			e.printStackTrace();
//		}
//	}
}
