package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.Service;
import com.scholastic.sbam.server.database.util.HibernateAccessor;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbService extends HibernateAccessor {
	
	static String objectName = Service.class.getSimpleName();
	
	public static Service getByCode(String code) {
		return (Service) getByField(objectName, "serviceCode", code, "description");
	}
	
	public static List<Service> findAll() {
		List<Object> results = findAll(objectName);
		List<Service> reasons = new ArrayList<Service>();
		for (int i = 0; i < results.size(); i++)
			reasons.add((Service) results.get(i));
		return reasons;
	}
	
	public static List<Service> findUndeleted() {
		return findFiltered(null, null, (char) 0, null, (char) 0, 'X');
	}
	
	public static List<Service> findFiltered(String code, String description, char serviceType, String exportValue, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (code != null && code.length() > 0)
            	crit.add(Restrictions.like("serviceCode", code));
            if (description != null && description.length() > 0)
            	crit.add(Restrictions.like("description", description));
            if (exportValue != null && exportValue.length() > 0)
            	crit.add(Restrictions.like("exportValue", exportValue));
            if (serviceType != 0)
            	crit.add(Restrictions.like("serviceType", serviceType));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("seq"));
            crit.addOrder(Order.asc("presentationPath"));
            crit.addOrder(Order.asc("description"));
            @SuppressWarnings("unchecked")
			List<Service> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<Service>();
	}
}
