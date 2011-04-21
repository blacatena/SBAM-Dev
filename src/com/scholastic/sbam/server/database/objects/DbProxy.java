package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.scholastic.sbam.server.database.codegen.Proxy;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.ProxyInstance;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbProxy extends HibernateAccessor {
	
	static String objectName = Proxy.class.getSimpleName();
	
	public static ProxyInstance getInstance(Proxy dbInstance) {
		ProxyInstance instance = new ProxyInstance();
		instance.setProxyId(dbInstance.getProxyId());
		instance.setDescription(dbInstance.getDescription());
		instance.setSearchKeys(dbInstance.getSearchKeys());
		instance.setNote(dbInstance.getNote());
		instance.setStatus(dbInstance.getStatus());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		
		return instance;
	}
	
	public static Proxy getById(int proxyId) {
		return (Proxy) getByField(objectName, "proxyId", proxyId, "description");
	}
	
	public static List<Proxy> findAll() {
		List<Object> results = findAll(objectName);
		List<Proxy> reasons = new ArrayList<Proxy>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((Proxy) results.get(i));
		return reasons;
	}
	
	public static List<Proxy> findFiltered(String filter, boolean doBoolean, char status, char neStatus, String sortCol, SortDir sortDirection) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
      
            filter = filter.replaceAll("'", "''");
			String sql;
			if (doBoolean) {
				sql = "MATCH (description, search_keys) AGAINST ('" + filter + "' IN BOOLEAN MODE)";
			} else {
				sql = "MATCH (description, search_keys) AGAINST ('" + filter + "')";
			}
			crit.add(Restrictions.sqlRestriction(sql));
            
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            
            if (sortCol != null && sortCol.length() > 0) {
            	if (sortDirection == SortDir.ASC)
            		crit.addOrder(Order.asc(sortCol));
            	else
            		crit.addOrder(Order.desc(sortCol));
            } else {
            	crit.addOrder(Order.asc("description"));
            }
            
            @SuppressWarnings("unchecked")
			List<Proxy> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<Proxy>();
	}
	
	public static List<Proxy> findFiltered(int proxyId, String description, String searchKeys, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (proxyId > 0)
            	crit.add(Restrictions.eq("proxyId", proxyId));
            if (description != null && description.length() > 0)
            	crit.add(Restrictions.like("description", description));
            if (searchKeys != null && searchKeys.length() > 0)
            	crit.add(Restrictions.like("searchKeys", description));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("description"));
            @SuppressWarnings("unchecked")
			List<Proxy> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<Proxy>();
	}
}
