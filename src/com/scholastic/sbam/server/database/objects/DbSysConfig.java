package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.scholastic.sbam.server.database.codegen.SysConfig;
import com.scholastic.sbam.server.database.util.HibernateAccessor;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbSysConfig extends HibernateAccessor {
	
	static String objectName = SysConfig.class.getSimpleName();
	
	public static SysConfig getByCode(String id) {
		return (SysConfig) getByField(objectName, "id", id, "seq");
	}
	
	public static List<SysConfig> findAll() {
		List<Object> results = findAll(objectName);
		List<SysConfig> reasons = new ArrayList<SysConfig>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((SysConfig) results.get(i));
		return reasons;
	}
	
	public static SysConfig getActive() {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            crit.addOrder(Order.asc("seq"));
            @SuppressWarnings("unchecked")
			List<SysConfig> objects = crit.list();
            if (objects.size() > 0)
            	return objects.get(0);
            else
            	return null;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
	}
}
