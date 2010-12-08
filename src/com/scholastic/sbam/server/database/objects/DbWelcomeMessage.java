package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.WelcomeMessage;
import com.scholastic.sbam.server.database.util.HibernateAccessor;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbWelcomeMessage extends HibernateAccessor {
	
	static String objectName = WelcomeMessage.class.getSimpleName();

    @SuppressWarnings("unchecked")
	public static List<WelcomeMessage> findToShow() throws Exception {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            crit.add(Restrictions.gt("expireDate", new Date()));
            crit.add(Restrictions.ne("deleted", "Y"));
            crit.addOrder(Order.asc("postDate"));
            List<WelcomeMessage> objects = crit.list();
            return objects;
//            List<WelcomeMessage> welcomeMessages = new ArrayList<WelcomeMessage>();
//            for (Object object: objects)
//            	welcomeMessages.add((WelcomeMessage) object);
//            return welcomeMessages;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            throw e;
        }
    //    return new ArrayList<WelcomeMessage>();
	}
	
	public static List<WelcomeMessage> findAll() {
		List<Object> results = findAll(objectName);
		List<WelcomeMessage> welcomeMessages = new ArrayList<WelcomeMessage>();
		for (int i = 0; i < results.size(); i++)
			welcomeMessages.add((WelcomeMessage) results.get(i));
		return welcomeMessages;
	}
}
