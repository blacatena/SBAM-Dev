package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.UserCache;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.UserCacheInstance;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbUserCache extends HibernateAccessor {
	
	static String objectName = UserCache.class.getSimpleName();
	
	public static UserCacheInstance getInstance(UserCache dbInstance) {
		UserCacheInstance instance = new UserCacheInstance();
		
		instance.setUserName(dbInstance.getId().getUserName());
		instance.setCategory(dbInstance.getId().getCategory());
		instance.setIntKey(dbInstance.getId().getIntKey());
		instance.setStrKey(dbInstance.getId().getStrKey());
		instance.setHint(dbInstance.getHint());
		instance.setAccessDatetime(dbInstance.getAccessDatetime());
		
		return instance;
	}
	
	public static UserCache getById(String userName, String category, int intKey, String strKey) {
		List<UserCache> results = findFiltered(userName, category, intKey, strKey, null);
		if (results == null || results.size() == 0)
			return null;
		return results.get(0);
	}
	
	public static List<UserCache> findByUserName(String userName) {
		return findFiltered(userName, null, -1, null, null);
	}
	
	public static List<UserCache> findByUserName(String userName, String category) {
		return findFiltered(userName, category, -1, null, null);
	}
	
	public static List<UserCache> findByUserName(String userName, Date fromDate) {
		return findFiltered(userName, null, -1, null, fromDate);
	}
	
	public static List<UserCache> findByUserName(String userName, String category, Date fromDate) {
		return findFiltered(userName, category, -1, null, fromDate);
	}
	
	public static List<UserCache> findToRestore(String userName) {
		return findFiltered(userName, null, -1, null, null);
	}
	
	public static List<UserCache> findFiltered(String userName, String category, int intKey, String strKey, Date fromDate) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (userName != null && userName.length() > 0)
            	crit.add(Restrictions.like("id.userName", userName));
            if (category != null && category.length() > 0)
            	crit.add(Restrictions.like("id.category", category));
            if (intKey >= 0)
            	crit.add(Restrictions.eq("id.intKey", intKey));
            if (strKey != null && strKey.length() > 0)
            	crit.add(Restrictions.eq("id.strKey", strKey));
            if (fromDate != null)
            	crit.add(Restrictions.ge("accessDatetime", fromDate));
            
            crit.addOrder(Order.desc("accessDatetime"));
            
            @SuppressWarnings("unchecked")
			List<UserCache> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<UserCache>();
	}
	
	public static List<UserCache> findFiltered(String userName, String category, Date beforeDate) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (userName != null && userName.length() > 0)
            	crit.add(Restrictions.like("id.userName", userName));
            if (category != null && category.length() > 0)
            	crit.add(Restrictions.like("id.category", category));
            if (beforeDate != null)
            	crit.add(Restrictions.le("accessDatetime", beforeDate));
            
            crit.addOrder(Order.desc("accessDatetime"));
            
            @SuppressWarnings("unchecked")
			List<UserCache> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<UserCache>();
	}
	
	public static List<UserCache> findAll() {
		List<Object> results = findAll(objectName);
		List<UserCache> userCaches = new ArrayList<UserCache>();
		for (int i = 0; i < results.size(); i++)
			userCaches.add((UserCache) results.get(i));
		return userCaches;
	}
}
