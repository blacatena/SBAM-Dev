package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.UcnConversion;
import com.scholastic.sbam.server.database.codegen.UcnConversionId;
import com.scholastic.sbam.server.database.util.HibernateAccessor;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbUcnConversion extends HibernateAccessor {
	
	static String objectName = UcnConversion.class.getSimpleName();
	
	public static UcnConversion getById(int ucn, int ucnSuffix) {
		UcnConversionId aeIpId = new UcnConversionId();
		aeIpId.setUcn(ucn);
		aeIpId.setUcnSuffix(ucnSuffix);
		return getById(aeIpId);
	}

	public static UcnConversion getById(UcnConversionId aeIpId) {
		try {
			UcnConversion instance = (UcnConversion) sessionFactory.getCurrentSession().get(getObjectReference(objectName), aeIpId);
//			if (instance == null) {
//				log.debug("get successful, no instance found");
//			} else {
//				log.debug("get successful, instance found");
//			}
			return instance;
		} catch (RuntimeException re) {
        	re.printStackTrace();
            System.out.println(re.getMessage());
			throw re;
		}
	}
	
	public static List<UcnConversion> findAll() {
		List<Object> results = findAll(objectName);
		List<UcnConversion> reasons = new ArrayList<UcnConversion>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((UcnConversion) results.get(i));
		return reasons;
	}
}
