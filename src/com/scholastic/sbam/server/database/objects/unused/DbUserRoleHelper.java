package com.scholastic.sbam.server.database.objects.unused;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.UserRole;

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

public class DbUserRoleHelper
{

    private static final long serialVersionUID = 0x1f1f0e46L;
    private static final Log log = LogFactory.getLog(DbUserRoleHelper.class);
    private static final SessionFactory sessionFactory = getSessionFactory();
    
    private static final String USER_ROLE = "com.scholastic.sbam.server.database.codegen.UserRole";

    public DbUserRoleHelper()
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

    public static void refresh(UserRole transientInstance)
    {
        log.debug("refreshing UserRole instance");
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

    public static void persist(UserRole transientInstance)
    {
        log.debug("persisting UserRole instance");
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

    public static void attachDirty(UserRole instance)
    {
        log.debug("attaching dirty UserRole instance");
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

    public static void attachClean(UserRole instance)
    {
        log.debug("attaching clean UserRole instance");
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

    public static void delete(UserRole persistentInstance)
    {
        log.debug("deleting UserRole instance");
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

    public static UserRole merge(UserRole detachedInstance)
    {
        log.debug("merging UserRole instance");
        try
        {
            UserRole result = (UserRole)sessionFactory.getCurrentSession().merge(detachedInstance);
            log.debug("merge successful");
            return result;
        }
        catch(RuntimeException re)
        {
            log.error("merge failed", re);
            throw re;
        }
    }

//    public static UserRole findById(Integer id)
//    {
//        log.debug("getting UserRole instance with id: " + id);
//        try
//        {
//            UserRole instance = (UserRole)sessionFactory.getCurrentSession().get("codegen.UserRole", id);
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

//    public static UserRole findById(String id)
//    {
//        return findById(Integer.parseInt(id));
//    }

    @SuppressWarnings("unchecked")
	public static UserRole findByUserRoleName(String userName, String roleName)
    {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(USER_ROLE);
            crit.add(Restrictions.eq("userName", userName));
            crit.add(Restrictions.eq("roleName", roleName));
            crit.addOrder(Order.asc("roleName"));
            List<UserRole> userroles = crit.list();
            if (userroles.size() == 0)
            	return null;
            else
            	return userroles.get(0);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
	public static List<UserRole> findByUserName(String userName)
    {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(USER_ROLE);
            crit.add(Restrictions.eq("userName", userName));
            crit.addOrder(Order.asc("roleName"));
            List<UserRole> userRoles = crit.list();
            return userRoles;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        return new ArrayList<UserRole>();
    }

    @SuppressWarnings("unchecked")
	public static List<UserRole> findByExample(UserRole instance)
    {
        log.debug("finding User instance by example");
        try
        {
            List<UserRole> results = sessionFactory.getCurrentSession().createCriteria(USER_ROLE).add(Example.create(instance)).list();
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
	public static List<UserRole> findAll()
    {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(USER_ROLE);
            crit.addOrder(Order.asc("userName"));
            crit.addOrder(Order.asc("roleName"));
            List <UserRole>users = crit.list();
            return users;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static void easyPersist(UserRole instance)
    {
        HibernateUtil.startTransaction();
        persist(instance);
        HibernateUtil.endTransaction();
    }

    public static void easyDelete(UserRole instance)
    {
        HibernateUtil.startTransaction();
        delete(instance);
        HibernateUtil.endTransaction();
    }

    public static void easyAttachDirty(UserRole instance)
    {
        HibernateUtil.startTransaction();
        attachDirty(instance);
        HibernateUtil.endTransaction();
    }

    public static void printUserRole(UserRole userRole)
    {
        System.out.println(userRole.getId().getUserName());
        System.out.println(userRole.getId().getRoleName());
        System.out.println(userRole.getReadWrite());
    }

}
