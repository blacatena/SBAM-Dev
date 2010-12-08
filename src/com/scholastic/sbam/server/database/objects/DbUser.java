package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

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
	
	public static List<User> findAll() {
		List<Object> results = findAll(objectName);
		List<User> users = new ArrayList<User>();
		for (int i = 0; i < results.size(); i++)
			users.add((User) results.get(i));
		return users;
	}
}
