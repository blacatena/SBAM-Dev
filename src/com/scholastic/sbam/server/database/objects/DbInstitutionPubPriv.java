package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.InstitutionPubPriv;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.InstitutionPubPrivInstance;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbInstitutionPubPriv extends HibernateAccessor {
	
	static String objectName = InstitutionPubPriv.class.getSimpleName();
	
	public static InstitutionPubPrivInstance getInstance(InstitutionPubPriv dbInstance) {
		InstitutionPubPrivInstance instance = new InstitutionPubPrivInstance();
		instance.setPublicPrivateCode(dbInstance.getPubPrivCode());
		instance.setShortName(dbInstance.getShortName());
		instance.setDescription(dbInstance.getDescription());
		
		return instance;
	}
	
	public static InstitutionPubPriv getByCode(String code) {
		return (InstitutionPubPriv) getByField(objectName, "publicPrivateCode", code, "description");
	}
	
	public static List<InstitutionPubPriv> findAll() {
		List<Object> results = findAll(objectName);
		List<InstitutionPubPriv> codes = new ArrayList<InstitutionPubPriv>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				codes.add((InstitutionPubPriv) results.get(i));
		return codes;
	}
}
