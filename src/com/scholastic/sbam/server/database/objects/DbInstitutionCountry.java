package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.InstitutionCountry;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.InstitutionCountryInstance;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbInstitutionCountry extends HibernateAccessor {
	
	static String objectName = InstitutionCountry.class.getSimpleName();
	
	public static InstitutionCountryInstance getInstance(InstitutionCountry dbInstance) {
		InstitutionCountryInstance instance = new InstitutionCountryInstance();
		instance.setCountryCode(dbInstance.getCountryCode());
		instance.setDescription(dbInstance.getDescription());
		
		return instance;
	}
	
	public static InstitutionCountry getByCode(String code) {
		return (InstitutionCountry) getByField(objectName, "countryCode", code, "description");
	}
	
	public static List<InstitutionCountry> findAll() {
		List<Object> results = findAll(objectName);
		List<InstitutionCountry> countries = new ArrayList<InstitutionCountry>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				countries.add((InstitutionCountry) results.get(i));
		return countries;
	}
}
