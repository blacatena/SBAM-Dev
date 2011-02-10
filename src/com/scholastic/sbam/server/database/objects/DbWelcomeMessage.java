package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.WelcomeMessage;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.WelcomeMessageInstance;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbWelcomeMessage extends HibernateAccessor {
	
	static String objectName = WelcomeMessage.class.getSimpleName();
	
	public static WelcomeMessageInstance getInstance(WelcomeMessage dbInstance) {
		WelcomeMessageInstance instance = new WelcomeMessageInstance();
		instance.setId(dbInstance.getId());
		instance.setTitle(dbInstance.getTitle());
		instance.setContent(dbInstance.getContent());
		instance.setExpireDate(dbInstance.getExpireDate());
		instance.setPostDate(dbInstance.getPostDate());
		instance.setStatus(dbInstance.getStatus());
		
		return instance;
	}
	
	public static WelcomeMessage getById(int id) {
		return (WelcomeMessage) HibernateAccessor.getById(objectName, id);
	}

    @SuppressWarnings("unchecked")
	public static List<WelcomeMessage> findToShow() throws Exception {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            crit.add(Restrictions.gt("expireDate", new Date()));
            crit.add(Restrictions.eq("status", AppConstants.STATUS_ACTIVE));
            crit.addOrder(Order.desc("postDate"));
            List<WelcomeMessage> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            throw e;
        }
	}

    @SuppressWarnings("unchecked")
	public static List<WelcomeMessage> findAfterExpireDate(Date expireDate) throws Exception {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (expireDate != null)
            	crit.add(Restrictions.gt("expireDate", expireDate));
            crit.add(Restrictions.ne("status", AppConstants.STATUS_DELETED));
            crit.addOrder(Order.desc("postDate"));
            List<WelcomeMessage> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            throw e;
        }
	}
	
	public static List<WelcomeMessage> findAll() {
		List<Object> results = findAll(objectName);
		List<WelcomeMessage> welcomeMessages = new ArrayList<WelcomeMessage>();
		for (int i = 0; i < results.size(); i++)
			welcomeMessages.add((WelcomeMessage) results.get(i));
		return welcomeMessages;
	}
}
