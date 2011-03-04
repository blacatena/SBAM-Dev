package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.InstitutionGroup;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.InstitutionGroupInstance;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbInstitutionGroup extends HibernateAccessor {
	
	static String objectName = InstitutionGroup.class.getSimpleName();
	
	public static InstitutionGroupInstance getInstance(InstitutionGroup dbInstance) {
		InstitutionGroupInstance instance = new InstitutionGroupInstance();
		instance.setGroupCode(dbInstance.getGroupCode());
		instance.setDescription(dbInstance.getDescription());
		
		return instance;
	}
	
	public static InstitutionGroup getByCode(String code) {
		return (InstitutionGroup) getByField(objectName, "groupCode", code, "description");
	}
	
	public static List<InstitutionGroup> findAll() {
		List<Object> results = findAll(objectName);
		List<InstitutionGroup> reasons = new ArrayList<InstitutionGroup>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((InstitutionGroup) results.get(i));
		return reasons;
	}
}
