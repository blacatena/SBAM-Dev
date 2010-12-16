package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.UserMessage;
import com.scholastic.sbam.server.database.util.HibernateAccessor;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbUserMessage extends HibernateAccessor {
	
	static String objectName = UserMessage.class.getSimpleName();

    @SuppressWarnings("unchecked")
	public static List<UserMessage> findToShow(String userName, String locationTag) throws Exception {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            crit.add(Restrictions.eq("userName", userName));
            crit.add(Restrictions.eq("locationTag", locationTag));
            crit.add(Restrictions.ne("status", "X"));
            crit.addOrder(Order.asc("windowPosZ"));
            List<UserMessage> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            throw e;
        }
	}
	
	public static List<UserMessage> findAll() {
		List<Object> results = findAll(objectName);
		List<UserMessage> welcomeMessages = new ArrayList<UserMessage>();
		for (int i = 0; i < results.size(); i++)
			welcomeMessages.add((UserMessage) results.get(i));
		return welcomeMessages;
	}
	
	public static UserMessage getById(int id) {
		return (UserMessage) getById(objectName, id);
	}
	
	public static UserMessage getById(String id) {
		return (UserMessage) getById(objectName, id);
	}
}
