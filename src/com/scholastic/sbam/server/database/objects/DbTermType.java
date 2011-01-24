package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.TermType;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.TermTypeInstance;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbTermType extends HibernateAccessor {
	
	static String objectName = TermType.class.getSimpleName();
	
	public static TermTypeInstance getInstance(TermType dbInstance) {
		TermTypeInstance instance = new TermTypeInstance();
		instance.setTermTypeCode(dbInstance.getTermTypeCode());
		instance.setDescription(dbInstance.getDescription());
		instance.setActivate(dbInstance.getActivate());
		instance.setStatus(dbInstance.getStatus());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		
		return instance;
	}
	
	public static TermType getByCode(String code) {
		return (TermType) getByField(objectName, "termTypeCode", code, "description");
	}
	
	public static TermType getByCode(char code) {
		return (TermType) getByField(objectName, "termTypeCode", code + "", "description");
	}
	
	public static List<TermType> findAll() {
		List<Object> results = findAll(objectName);
		List<TermType> reasons = new ArrayList<TermType>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((TermType) results.get(i));
		return reasons;
	}
	
	public static List<TermType> findFiltered(String code, String description, char activate, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (code != null && code.length() > 0)
            	crit.add(Restrictions.like("termTypeCode", code));
            if (description != null && description.length() > 0)
            	crit.add(Restrictions.like("description", description));
            if (activate != 0)
            	crit.add(Restrictions.like("activate", activate));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("description"));
            @SuppressWarnings("unchecked")
			List<TermType> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<TermType>();
	}
}
