package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.InstitutionType;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.InstitutionTypeInstance;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbInstitutionType extends HibernateAccessor {
	
	static String objectName = InstitutionType.class.getSimpleName();
	
	public static InstitutionTypeInstance getInstance(InstitutionType dbInstance) {
		InstitutionTypeInstance instance = new InstitutionTypeInstance();
		instance.setTypeCode(dbInstance.getTypeCode());
		instance.setDescription(dbInstance.getDescription());
		instance.setLongDescription(dbInstance.getLongDescription());
		instance.setGroupCode(dbInstance.getGroupCode());
		
		return instance;
	}
	
	public static InstitutionType getByCode(String code) {
		return (InstitutionType) getByField(objectName, "typeCode", code, "description");
	}
	
	public static List<InstitutionType> findAll() {
		List<Object> results = findAll(objectName);
		List<InstitutionType> reasons = new ArrayList<InstitutionType>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((InstitutionType) results.get(i));
		return reasons;
	}
}
