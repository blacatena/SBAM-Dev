package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.UserPortletCache;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.UserPortletCacheInstance;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbUserPortletCache extends HibernateAccessor {
	
	static String objectName = UserPortletCache.class.getSimpleName();
	
	public static UserPortletCacheInstance getInstance(UserPortletCache dbInstance) {
		UserPortletCacheInstance instance = new UserPortletCacheInstance();
		
		instance.setUserName(dbInstance.getId().getUserName());
		instance.setPortletId(dbInstance.getId().getPortletId());
		instance.setPortletType(dbInstance.getPortletType());
		instance.setKeyData(dbInstance.getKeyData());
		instance.setRestoreColumn(dbInstance.getRestoreColumn());
		instance.setRestoreRow(dbInstance.getRestoreRow());
		instance.setRestoreWidth(dbInstance.getRestoreWidth());
		instance.setRestoreHeight(dbInstance.getRestoreHeight());
		instance.setMinimized(dbInstance.getMinimized());
		
		return instance;
	}
	
	public static UserPortletCache getById(String userName, int portletId) {
		List<UserPortletCache> results = findFiltered(userName, portletId, null, null);
		if (results == null || results.size() == 0)
			return null;
		return results.get(0);
	}
	
	public static List<UserPortletCache> findByUserName(String userName) {
		return findFiltered(userName, -1, null, null);
	}
	
	public static List<UserPortletCache> findByUserName(String userName, String portletType) {
		return findFiltered(userName, -1, portletType, null);
	}
	
	public static List<UserPortletCache> findFiltered(String userName, int portletId, String portletType, String keyData) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (userName != null && userName.length() > 0)
            	crit.add(Restrictions.like("id.userName", userName));
            if (portletId >= 0)
            	crit.add(Restrictions.eq("id.portletId", portletId));
            if (portletType != null && portletType.length() > 0)
            	crit.add(Restrictions.like("portletType", portletType));
            if (keyData != null && keyData.length() > 0)
            	crit.add(Restrictions.eq("id.keyData", keyData));
            
           	crit.addOrder(Order.asc("restoreColumn"));
           	crit.addOrder(Order.asc("restoreRow"));
           	crit.addOrder(Order.asc("id.portletId"));
            
            @SuppressWarnings("unchecked")
			List<UserPortletCache> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<UserPortletCache>();
	}
	
	public static List<UserPortletCache> findAll() {
		List<Object> results = findAll(objectName);
		List<UserPortletCache> userCaches = new ArrayList<UserPortletCache>();
		for (int i = 0; i < results.size(); i++)
			userCaches.add((UserPortletCache) results.get(i));
		return userCaches;
	}
}
