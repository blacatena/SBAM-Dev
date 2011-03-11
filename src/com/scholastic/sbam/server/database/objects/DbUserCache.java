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
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbUserCache extends HibernateAccessor {
	
	public static final int SORT_BY_DATE		= 0;
	public static final int SORT_BY_POSITION	= 1;
	
	static String objectName = UserCache.class.getSimpleName();
	
	public static UserCacheInstance getInstance(UserCache dbInstance) {
		UserCacheInstance instance = new UserCacheInstance();
		
		instance.setUserName(dbInstance.getId().getUserName());
		instance.setCategory(dbInstance.getId().getCategory());
		instance.setIntKey(dbInstance.getId().getIntKey());
		instance.setStrKey(dbInstance.getId().getStrKey());
		instance.setRestoreColumn(dbInstance.getRestoreColumn());
		instance.setRestoreRow(dbInstance.getRestoreRow());
		instance.setRestoreState(dbInstance.getRestoreState());
		
		return instance;
	}
	
	public static UserCache getById(String userName, String category, int intKey, String strKey) {
		List<UserCache> results = findFiltered(userName, category, intKey, strKey, AppConstants.STATUS_ANY_NONE, null, AppConstants.STATUS_ANY_NONE);
		if (results == null || results.size() == 0)
			return null;
		return results.get(0);
	}
	
	public static List<UserCache> findByUserName(String userName) {
		return findFiltered(userName, null, -1, null, AppConstants.STATUS_ANY_NONE, null, AppConstants.STATUS_ANY_NONE);
	}
	
	public static List<UserCache> findByUserName(String userName, String category) {
		return findFiltered(userName, category, -1, null, AppConstants.STATUS_ANY_NONE, null, AppConstants.STATUS_ANY_NONE);
	}
	
	public static List<UserCache> findByUserName(String userName, Date fromDate) {
		return findFiltered(userName, null, -1, null, AppConstants.STATUS_ANY_NONE, fromDate, AppConstants.STATUS_ANY_NONE);
	}
	
	public static List<UserCache> findByUserName(String userName, String category, Date fromDate) {
		return findFiltered(userName, category, -1, null, AppConstants.STATUS_ANY_NONE, fromDate, AppConstants.STATUS_ANY_NONE);
	}
	
	public static List<UserCache> findToRestore(String userName) {
		return findFiltered(userName, null, -1, null, AppConstants.STATUS_ANY_NONE, null, AppConstants.RESTORE_NO, SORT_BY_POSITION);
	}
	
	public static List<UserCache> findFiltered(String userName, String category, int intKey, String strKey, char restoreState, Date fromDate, char neRestoreState) {
		return findFiltered( userName,  category, intKey, strKey,  restoreState, fromDate, neRestoreState, SORT_BY_DATE);
	}
	
	public static List<UserCache> findFiltered(String userName, String category, int intKey, String strKey, char restoreState, Date fromDate, char neRestoreState, int sortType) {
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
            if (restoreState != AppConstants.STATUS_ANY_NONE)
            	crit.add(Restrictions.eq("restoreState", restoreState));
            if (fromDate != null)
            	crit.add(Restrictions.ge("accessDate", fromDate));
            if (neRestoreState != AppConstants.STATUS_ANY_NONE)
            	crit.add(Restrictions.ne("restoreState", restoreState));
            
            //	When doing a restore for a portal, sort this way
            if (sortType == SORT_BY_POSITION) {
               	crit.addOrder(Order.asc("restoreColumn"));
               	crit.addOrder(Order.asc("restoreRow"));
            }
            
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
	
	public static List<UserCache> findFiltered(String userName, String firstName, String lastName, String email) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (userName != null && userName.length() > 0)
            	crit.add(Restrictions.like("userName", userName));
            if (firstName != null && firstName.length() > 0)
            	crit.add(Restrictions.like("firstName", firstName));
            if (lastName != null && lastName.length() > 0)
            	crit.add(Restrictions.like("lastName", lastName));
            if (email != null && email.length() > 0)
            	crit.add(Restrictions.like("email", email));
            crit.addOrder(Order.asc("userName"));
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
