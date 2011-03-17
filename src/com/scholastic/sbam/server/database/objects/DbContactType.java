package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.ContactType;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.ContactTypeInstance;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbContactType extends HibernateAccessor {
	
	static String objectName = ContactType.class.getSimpleName();
	
	public static ContactTypeInstance getInstance(ContactType dbInstance) {
		ContactTypeInstance instance = new ContactTypeInstance();
		
		instance.setContactTypeCode(dbInstance.getContactTypeCode());
		instance.setDescription(dbInstance.getDescription());
		instance.setStatus(dbInstance.getStatus());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		
		return instance;
	}
	
	public static ContactType getByCode(String code) {
		return (ContactType) getByField(objectName, "contactTypeCode", code, "description");
	}
	
	public static ContactType getByCode(char code) {
		return (ContactType) getByField(objectName, "contactTypeCode", code + "", "description");
	}
	
	public static List<ContactType> findAll() {
		List<Object> results = findAll(objectName);
		List<ContactType> reasons = new ArrayList<ContactType>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((ContactType) results.get(i));
		return reasons;
	}
	
	public static List<ContactType> findFiltered(String code, String description, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (code != null && code.length() > 0)
            	crit.add(Restrictions.like("contactTypeCode", code));
            if (description != null && description.length() > 0)
            	crit.add(Restrictions.like("description", description));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("description"));
            @SuppressWarnings("unchecked")
			List<ContactType> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<ContactType>();
	}
}
