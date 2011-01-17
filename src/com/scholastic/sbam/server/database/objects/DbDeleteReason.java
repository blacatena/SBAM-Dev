package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.DeleteReason;
import com.scholastic.sbam.server.database.util.HibernateAccessor;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbDeleteReason extends HibernateAccessor {
	
	static String objectName = DeleteReason.class.getSimpleName();
	
	public static DeleteReason getByCode(String code) {
		return (DeleteReason) getByField(objectName, "deleteReasonCode", code, "description");
	}
	
	public static List<DeleteReason> findAll() {
		List<Object> results = findAll(objectName);
		List<DeleteReason> reasons = new ArrayList<DeleteReason>();
		for (int i = 0; i < results.size(); i++)
			reasons.add((DeleteReason) results.get(i));
		return reasons;
	}
	
	public static List<DeleteReason> findFiltered(String code, String description, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (code != null && code.length() > 0)
            	crit.add(Restrictions.like("deleteReasonCode", code));
            if (description != null && description.length() > 0)
            	crit.add(Restrictions.like("description", description));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("description"));
            @SuppressWarnings("unchecked")
			List<DeleteReason> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<DeleteReason>();
	}
}
