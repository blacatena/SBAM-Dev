package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.Documentation;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.DocumentationInstance;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbDocumentation extends HibernateAccessor {
	
	static String objectName = Documentation.class.getSimpleName();
	
	public static DocumentationInstance getInstance(Documentation dbInstance) {
		DocumentationInstance instance = new DocumentationInstance();
		
		instance.setId(dbInstance.getId());
		instance.setTitle(dbInstance.getTitle());
		instance.setSeq(dbInstance.getSeq());
		instance.setTypes(dbInstance.getTypes());
		instance.setDocVersion(dbInstance.getDocVersion());
		instance.setDescription(dbInstance.getDescription());
		instance.setLink(dbInstance.getLink());
		instance.setIconImage(dbInstance.getIconImage());
		instance.setUpdatedDatetime(dbInstance.getUpdatedDatetime());
		instance.setStatus(dbInstance.getStatus());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		
		return instance;
	}
	
	public static List<Documentation> findAll() {
		List<Object> results = findAll(objectName);
		List<Documentation> reasons = new ArrayList<Documentation>();
		for (int i = 0; i < results.size(); i++)
			reasons.add((Documentation) results.get(i));
		return reasons;
	}
	
	public static List<Documentation> findUndeleted() {
		return findFiltered(null, (char) 0, 'X');
	}
	
	public static List<Documentation> findFiltered(String title, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (title != null && title.length() > 0)
            	crit.add(Restrictions.like("title", title));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("seq"));
            crit.addOrder(Order.asc("title"));
            @SuppressWarnings("unchecked")
			List<Documentation> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<Documentation>();
	}
}
