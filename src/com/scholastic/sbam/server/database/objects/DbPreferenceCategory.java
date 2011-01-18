package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.PreferenceCategory;
import com.scholastic.sbam.server.database.util.HibernateAccessor;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbPreferenceCategory extends HibernateAccessor {
	
	static String objectName = PreferenceCategory.class.getSimpleName();
	
	public static PreferenceCategory getByCode(String code) {
		return (PreferenceCategory) getByField(objectName, "prefCatCode", code, "description");
	}
	
	public static List<PreferenceCategory> findAll() {
		List<Object> results = findAll(objectName);
		List<PreferenceCategory> reasons = new ArrayList<PreferenceCategory>();
		for (int i = 0; i < results.size(); i++)
			reasons.add((PreferenceCategory) results.get(i));
		return reasons;
	}
	
	public static List<PreferenceCategory> findFiltered(String code, String description, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (code != null && code.length() > 0)
            	crit.add(Restrictions.like("prefCatCode", code));
            if (description != null && description.length() > 0)
            	crit.add(Restrictions.like("description", description));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("seq"));
            @SuppressWarnings("unchecked")
			List<PreferenceCategory> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<PreferenceCategory>();
	}
}
