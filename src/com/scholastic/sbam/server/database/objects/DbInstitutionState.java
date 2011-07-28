package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.InstitutionState;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.InstitutionStateInstance;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbInstitutionState extends HibernateAccessor {
	
	static String objectName = InstitutionState.class.getSimpleName();
	
	public static InstitutionStateInstance getInstance(InstitutionState dbInstance) {
		InstitutionStateInstance instance = new InstitutionStateInstance();
		instance.setStateCode(dbInstance.getStateCode());
		instance.setDescription(dbInstance.getDescription());
		instance.setCountryCode(dbInstance.getCountryCode());
		
		return instance;
	}
	
	public static InstitutionState getByCode(String code) {
		return (InstitutionState) getByField(objectName, "stateCode", code, "description");
	}
	
	public static List<InstitutionState> findAll() {
		List<Object> results = findAll(objectName);
		List<InstitutionState> states = new ArrayList<InstitutionState>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				states.add((InstitutionState) results.get(i));
		return states;
	}
}
