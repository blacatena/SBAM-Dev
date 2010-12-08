package com.scholastic.sbam.server.database.objects.unused;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.User;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.*;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.util.HibernateUtil;

/**
 * This class provides a template for accessing a table, using a different helper for each defined table.
 * 
 * The preferred method is to extend the HibernateAccessor class, using shared methods for common functions like persist and refresh, with type casting where necessary,
 * and writing custom find methods for access by keys (with or without type casting).
 * 
 * @author Bob Lacatena
 *
 */

public class DbUserHelper
{

    private static final long serialVersionUID = 0x1f1f0e46L;
    private static final Log log = LogFactory.getLog(DbUserHelper.class);
    private static final SessionFactory sessionFactory = getSessionFactory();
    
    private static final String USER = "com.scholastic.sbam.server.database.codegen.User";

    public DbUserHelper()
    {
    }

    protected static SessionFactory getSessionFactory()
    {
        try
        {
            return HibernateUtil.getSessionFactory();
        }
        catch(Exception e)
        {
            log.error("Could not locate SessionFactory in JNDI", e);
        }
        throw new IllegalStateException("Could not locate SessionFactory in JNDI");
    }

    public static void refresh(User transientInstance)
    {
        log.debug("refreshing User instance");
        try
        {
            sessionFactory.getCurrentSession().refresh(transientInstance);
            log.debug("refresh successful");
        }
        catch(RuntimeException re)
        {
            log.error("refresh failed", re);
            throw re;
        }
    }

    public static void persist(User transientInstance)
    {
        log.debug("persisting User instance");
        try
        {
            sessionFactory.getCurrentSession().persist(transientInstance);
            log.debug("persist successful");
        }
        catch(RuntimeException re)
        {
            log.error("persist failed", re);
            throw re;
        }
    }

    public static void attachDirty(User instance)
    {
        log.debug("attaching dirty User instance");
        try
        {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            log.debug("attach successful");
        }
        catch(RuntimeException re)
        {
            log.error("attach failed", re);
            throw re;
        }
    }

    public static void attachClean(User instance)
    {
        log.debug("attaching clean User instance");
        try
        {
            sessionFactory.getCurrentSession().buildLockRequest(LockOptions.NONE).lock(instance);  // lock(instance, LockMode.NONE);
            log.debug("attach successful");
        }
        catch(RuntimeException re)
        {
            log.error("attach failed", re);
            throw re;
        }
    }

    public static void delete(User persistentInstance)
    {
        log.debug("deleting User instance");
        try
        {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            log.debug("delete successful");
        }
        catch(RuntimeException re)
        {
            log.error("delete failed", re);
            throw re;
        }
    }

    public static User merge(User detachedInstance)
    {
        log.debug("merging User instance");
        try
        {
            User result = (User)sessionFactory.getCurrentSession().merge(detachedInstance);
            log.debug("merge successful");
            return result;
        }
        catch(RuntimeException re)
        {
            log.error("merge failed", re);
            throw re;
        }
    }

//    public static User findById(Integer id)
//    {
//        log.debug("getting User instance with id: " + id);
//        try
//        {
//            User instance = (User)sessionFactory.getCurrentSession().get("codegen.User", id);
//            if(instance == null)
//                log.debug("get successful, no instance found");
//            else
//                log.debug("get successful, instance found");
//            return instance;
//        }
//        catch(RuntimeException re)
//        {
//            log.error("get failed", re);
//            throw re;
//        }
//    }

//    public static User findById(String id)
//    {
//        return findById(Integer.parseInt(id));
//    }

    @SuppressWarnings("unchecked")
	public static User findByUserName(String userName)
    {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(USER);
            crit.add(Restrictions.eq("userName", userName));
            crit.addOrder(Order.asc("userName"));
            List<User> users = crit.list();
            if (users.size() == 0)
            	return null;
            else
            	return users.get(0);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
	public static List<User> findByFirstName(String firstName)
    {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(USER);
            crit.add(Restrictions.eq("firstName", firstName));
            crit.addOrder(Order.asc("userName"));
            List<User> users = crit.list();
            return users;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        return new ArrayList<User>();
    }

    @SuppressWarnings("unchecked")
	public static List<User> findByExample(User instance)
    {
        log.debug("finding User instance by example");
        try
        {
            List<User> results = sessionFactory.getCurrentSession().createCriteria(USER).add(Example.create(instance)).list();
            log.debug("find by example successful, result size: " + results.size());
            return results;
        }
        catch(RuntimeException re)
        {
            log.error("find by example failed", re);
            throw re;
        }
    }

    @SuppressWarnings("unchecked")
	public static List<User> findAll()
    {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(USER);
            crit.addOrder(Order.asc("UserName"));
            List <User>users = crit.list();
            return users;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static void easyPersist(User instance)
    {
        HibernateUtil.startTransaction();
        persist(instance);
        HibernateUtil.endTransaction();
    }

    public static void easyDelete(User instance)
    {
        HibernateUtil.startTransaction();
        delete(instance);
        HibernateUtil.endTransaction();
    }

    public static void easyAttachDirty(User instance)
    {
        HibernateUtil.startTransaction();
        attachDirty(instance);
        HibernateUtil.endTransaction();
    }

    public static void printUser(User User)
    {
        System.out.println(User.getUserName());
        System.out.println(User.getPassword());
        System.out.println(User.getFirstName());
        System.out.println(User.getLastName());
    }

}
