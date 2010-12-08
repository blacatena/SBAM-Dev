package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.UserRole;
import com.scholastic.sbam.server.database.util.HibernateAccessor;

/**
 * Database user_role table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbUserRole extends HibernateAccessor {
	
	static String objectName = UserRole.class.getSimpleName();

    @SuppressWarnings("unchecked")
	public static UserRole getByUserRole(String userName, String roleName)
    {
        try
        {
        	Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            crit.add(Restrictions.eq("id.userName", userName));
            crit.add(Restrictions.eq("id.roleName", roleName));
        	crit.addOrder(Order.asc("roleName"));
            List<Object> objects = crit.list();
            if (objects.size() > 0)
            	return (UserRole) objects.get(0);
            return null;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }
	
	public static List<UserRole> findByUserName(String userName) {
		List<Object> results = findByField(objectName, "id.userName", userName, "id.roleName");
		List<UserRole> userroles = new ArrayList<UserRole>();
		for (int i = 0; i < results.size(); i++)
			userroles.add((UserRole) results.get(i));
		return userroles;
	}
}
