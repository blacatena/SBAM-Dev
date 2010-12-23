package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.User;
import com.scholastic.sbam.server.database.util.HibernateAccessor;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbUser extends HibernateAccessor {
	
	static String objectName = User.class.getSimpleName();
	
	public static User getById(Integer id) {
		return (User) getById(objectName, id);
	}
	
	public static User getByUserName(String userName) {
		return (User) getByField(objectName, "userName", userName, "lastName");
	}
	
	public static List<User> findByLastName(String lastName) {
		List<Object> results = findByField(objectName, "lastName", lastName, "firstName");
		List<User> users = new ArrayList<User>();
		for (int i = 0; i < results.size(); i++)
			users.add((User) results.get(i));
		return users;
	}
	
	public static List<User> findFiltered(String userName, String firstName, String lastName, String email) {
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
			List<User> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<User>();
	}
	
	public static List<User> findAll() {
		List<Object> results = findAll(objectName);
		List<User> users = new ArrayList<User>();
		for (int i = 0; i < results.size(); i++)
			users.add((User) results.get(i));
		return users;
	}
}
