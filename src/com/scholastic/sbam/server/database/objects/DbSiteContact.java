package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.SiteContact;
import com.scholastic.sbam.server.database.codegen.SiteContactId;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.SiteContactInstance;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbSiteContact extends HibernateAccessor {
	
	static String objectName = SiteContact.class.getSimpleName();
	
	public static SiteContactInstance getInstance(SiteContact dbInstance) {
		SiteContactInstance instance = new SiteContactInstance();

		instance.setUcn(dbInstance.getId().getUcn());
		instance.setContactId(dbInstance.getId().getContactId());
		
		instance.setStatus(dbInstance.getStatus());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		
		return instance;
	}
	
	public static SiteContact getById(int ucn, int contactId) {
		SiteContactId scid = new SiteContactId();
		scid.setUcn(ucn);
		scid.setContactId(contactId);
		try {
			SiteContact instance = (SiteContact) sessionFactory.getCurrentSession().get(getObjectReference(objectName), scid);
			return instance;
		} catch (RuntimeException re) {
        	re.printStackTrace();
            System.out.println(re.getMessage());
			throw re;
		}
	}
	
	public static List<SiteContact> findAll() {
		List<Object> results = findAll(objectName);
		List<SiteContact> reasons = new ArrayList<SiteContact>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((SiteContact) results.get(i));
		return reasons;
	}
	
	public static List<SiteContact> findByUcn(int ucn, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (ucn > 0)
            	crit.add(Restrictions.eq("id.ucn", ucn));
            if (ucn > 1)
            	crit.add(Restrictions.eq("id.ucnSuffix", ucn));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("id.contactId"));
            @SuppressWarnings("unchecked")
			List<SiteContact> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<SiteContact>();
	}
	
	public static List<SiteContact> findByContactId(int contactId, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (contactId > 0)
            	crit.add(Restrictions.eq("id.contactId", contactId));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("id.ucn"));
            @SuppressWarnings("unchecked")
			List<SiteContact> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<SiteContact>();
	}
}
