package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.LinkType;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.LinkTypeInstance;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbLinkType extends HibernateAccessor {
	
	static String objectName = LinkType.class.getSimpleName();
	
	public static LinkTypeInstance getInstance(LinkType dbInstance) {
		LinkTypeInstance instance = new LinkTypeInstance();
		instance.setLinkTypeCode(dbInstance.getLinkTypeCode());
		instance.setDescription(dbInstance.getDescription());
		instance.setStatus(dbInstance.getStatus());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		
		return instance;
	}
	
	public static LinkType getByCode(String code) {
		return (LinkType) getByField(objectName, "linkTypeCode", code, "description");
	}
	
	public static LinkType getByCode(char code) {
		return (LinkType) getByField(objectName, "linkTypeCode", code + "", "description");
	}
	
	public static List<LinkType> findAll() {
		List<Object> results = findAll(objectName);
		List<LinkType> reasons = new ArrayList<LinkType>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((LinkType) results.get(i));
		return reasons;
	}
	
	public static List<LinkType> findFiltered(String code, String description, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (code != null && code.length() > 0)
            	crit.add(Restrictions.like("linkTypeCode", code));
            if (description != null && description.length() > 0)
            	crit.add(Restrictions.like("description", description));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("description"));
            @SuppressWarnings("unchecked")
			List<LinkType> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<LinkType>();
	}
}
