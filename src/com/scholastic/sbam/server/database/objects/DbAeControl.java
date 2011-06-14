package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.AeControl;
import com.scholastic.sbam.server.database.util.HibernateAccessor;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbAeControl extends HibernateAccessor {
	
	static String objectName = AeControl.class.getSimpleName();
	
	public static AeControl getById(int aeId) {
		return (AeControl) getByField(objectName, "aeId", aeId, "status");
	}
	
	public static List<AeControl> findAll() {
		List<Object> results = findAll(objectName);
		List<AeControl> reasons = new ArrayList<AeControl>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((AeControl) results.get(i));
		return reasons;
	}
	
	public static AeControl getLastComplete() throws Exception {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            crit.add(Restrictions.eq("status", 'c'));
            crit.addOrder(Order.desc("completedDatetime"));
            @SuppressWarnings("unchecked")
			List<AeControl> objects = crit.list();
            if (objects.size() < 1)
            	return null;
            return objects.get(0);
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
            throw e;
        }
	}
}
