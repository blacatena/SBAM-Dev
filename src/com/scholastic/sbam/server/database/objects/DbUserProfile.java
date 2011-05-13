package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.UserProfile;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.UserProfileInstance;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbUserProfile extends HibernateAccessor {
	
	static String objectName = UserProfile.class.getSimpleName();
	
	public static UserProfileInstance getInstance(UserProfile dbInstance) {
		if (dbInstance == null)
			return UserProfileInstance.getDefaultInstance();
		
		UserProfileInstance instance = new UserProfileInstance();
		
		/** TODO
		 *  Instantiate all profile fields from db entity
		 */
		
		return instance;
	}
	
	public static UserProfile getByUserName(String rename) {
		return (UserProfile) getByField(objectName, "userName", rename, "userName");
	}
	
	public static List<UserProfile> findAll() {
		List<Object> results = findAll(objectName);
		List<UserProfile> reasons = new ArrayList<UserProfile>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((UserProfile) results.get(i));
		return reasons;
	}
	
//	public static List<UserProfile> findFiltered(String code, String description, char status, char neStatus) {
//        try
//        {
//            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
//            if (code != null && code.length() > 0)
//            	crit.add(Restrictions.like("userName", code));
//            if (description != null && description.length() > 0)
//            	crit.add(Restrictions.like("description", description));
//            if (status != 0)
//            	crit.add(Restrictions.like("status", status));
//            if (neStatus != 0)
//            	crit.add(Restrictions.ne("status", neStatus));
//            crit.addOrder(Order.asc("description"));
//            @SuppressWarnings("unchecked")
//			List<UserProfile> objects = crit.list();
//            return objects;
//        }
//        catch(Exception e)
//        {
//        	e.printStackTrace();
//            System.out.println(e.getMessage());
//        }
//        return new ArrayList<UserProfile>();
//	}
}
