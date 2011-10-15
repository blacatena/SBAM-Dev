package com.scholastic.sbam.server.database.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.*;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.util.HibernateUtil;

/**
 * 
 * Base class for any database access or database access objects.  Most classes are static.
 * 
 * This class provides standard functions such as refresh, persist and base getter and finder methods.
 * 
 * @author Bob Lacatena
 *
 */
public class HibernateAccessor
{

	protected static final Log log = LogFactory.getLog(HibernateAccessor.class);
    protected static final SessionFactory sessionFactory = getSessionFactory();
    
    private static final String GEN_BASE = "com.scholastic.sbam.server.database.codegen";

    public HibernateAccessor()
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

    public static void refresh(Object transientInstance)
    {
        log.debug("refreshing Object instance");
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

    public static void persist(Object transientInstance)
    {
        log.debug("persisting Object instance");
        try
        {
            sessionFactory.getCurrentSession().persist(transientInstance);
            log.debug("persist successful");
        }
        catch(RuntimeException re)
        {
            log.error("persist failed", re);
            System.out.println(re);
            re.printStackTrace();
            throw re;
        }
    }

    public static void saveOrUpdate(Object transientInstance)
    {
        log.debug("save or update Object instance");
        try
        {
            sessionFactory.getCurrentSession().saveOrUpdate(transientInstance);
            log.debug("saveOrUpdate successful");
        }
        catch(RuntimeException re)
        {
            log.error("saveOrUpdate failed", re);
            System.out.println(re);
            re.printStackTrace();
            throw re;
        }
    }

    public static void save(Object transientInstance)
    {
        log.debug("save Object instance");
        try
        {
            sessionFactory.getCurrentSession().save(transientInstance);
            log.debug("save successful");
        }
        catch(RuntimeException re)
        {
            log.error("save failed", re);
            System.out.println(re);
            re.printStackTrace();
            throw re;
        }
    }

    public static void commit()
    {
        log.debug("refreshing Object instance");
        try
        {
            sessionFactory.getCurrentSession().getTransaction().commit();
            log.debug("commit successful");
        }
        catch(RuntimeException re)
        {
            log.error("commit failed", re);
            throw re;
        }
    }

    public static void attachDirty(Object instance)
    {
        log.debug("attaching dirty Object instance");
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

    public static void attachClean(Object instance)
    {
        log.debug("attaching clean Object instance");
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

    public static void delete(Object persistentInstance)
    {
        log.debug("deleting Object instance");
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

    public static Object merge(Object detachedInstance)
    {
        log.debug("merging Object instance");
        try
        {
            Object result = (Object)sessionFactory.getCurrentSession().merge(detachedInstance);
            log.debug("merge successful");
            return result;
        }
        catch(RuntimeException re)
        {
            log.error("merge failed", re);
            throw re;
        }
    }
    
    public static String getObjectReference(Object instance) {
    	return instance.getClass().getName();
    }
    
    public static String getObjectReference(String objectName) {
    	return GEN_BASE + "." + objectName;
    }

    public static Object getById(String objectName, Integer id)
    {
        log.debug("getting Object instance with id: " + id);
        try
        {
            Object instance = (Object)sessionFactory.getCurrentSession().get(getObjectReference(objectName), id);
            if(instance == null)
                log.debug("get successful, no instance found");
            else
                log.debug("get successful, instance found");
            return instance;
        }
        catch(RuntimeException re)
        {
            log.error("get failed", re);
            throw re;
        }
    }

    public static Object getById(Object instance, Integer id)
    {
    	return getById(instance.getClass().getName(), id);
    }

    public static Object getById(String objectName, String id)
    {
        return getById(objectName, Integer.parseInt(id));
    }

    public static Object findById(Object instance, String id)
    {
        return getById(instance.getClass().getName(), Integer.parseInt(id));
    }

//    @SuppressWarnings("unchecked")
//	public static Object findByObjectName(String ObjectName)
//    {
//        try
//        {
//            Criteria crit = sessionFactory.getCurrentSession().createCriteria(Object);
//            crit.add(Restrictions.eq("ObjectName", ObjectName));
//            crit.addOrder(Order.asc("ObjectName"));
//            List<Object> objects = crit.list();
//            if (objects.size() == 0)
//            	return null;
//            else
//            	return objects.get(0);
//        }
//        catch(Exception e)
//        {
//            System.out.println(e.getMessage());
//        }
//        return null;
//    }


//  @SuppressWarnings("unchecked")
	public static Object getByField(String objectName, String fieldName, String fieldValue)
    {
    	return getByField(objectName, fieldName, fieldValue, null);
    }

    @SuppressWarnings("unchecked")
	public static Object getByField(String objectName, String fieldName, String fieldValue, String sortField)
    {
        try
        {
        	Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            crit.add(Restrictions.eq(fieldName, fieldValue));
            if (sortField != null && sortField.length() > 0)
            	crit.addOrder(Order.asc(sortField));
            List<Object> objects = crit.list();
            if (objects.size() > 0)
            	return objects.get(0);
            return null;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
	public static Object getByField(String objectName, String fieldName, int fieldValue, String sortField)
    {
        try
        {
        	Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            crit.add(Restrictions.eq(fieldName, fieldValue));
            if (sortField != null && sortField.length() > 0)
            	crit.addOrder(Order.asc(sortField));
            List<Object> objects = crit.list();
            if (objects.size() > 0)
            	return objects.get(0);
            return null;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


//  @SuppressWarnings("unchecked")
	public static List<Object> findByField(String objectName, String fieldName, String fieldValue)
    {
    	return findByField(objectName, fieldName, fieldValue, null);
    }

    @SuppressWarnings("unchecked")
	public static List<Object> findByField(String objectName, String fieldName, String fieldValue, String sortField)
    {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            crit.add(Restrictions.eq(fieldName, fieldValue));
            if (sortField != null && sortField.length() > 0)
            	crit.addOrder(Order.asc(sortField));
            List<Object> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<Object>();
    }

    @SuppressWarnings("unchecked")
	public static List<Object> findByExample(Object instance)
    {
        log.debug("finding Object instance by example");
        try
        {
            List<Object> results = sessionFactory.getCurrentSession().createCriteria(getObjectReference(instance)).add(Example.create(instance)).list();
            log.debug("find by example successful, result size: " + results.size());
            return results;
        }
        catch(RuntimeException re)
        {
            log.error("find by example failed", re);
            throw re;
        }
    }

	public static List<Object> findAll(Object instance)
    {
    	return findAll(instance.getClass().getName());
    }

    @SuppressWarnings("unchecked")
	public static List<Object> findAll(String objectName)
    {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
        //  crit.addOrder(Order.asc("ObjectName"));
            List <Object>objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }
    
    public static int count(String tableName) throws SQLException {
		StringBuffer sql = new StringBuffer();
		sql.append("select count(*) as entry_count from ");
		sql.append(tableName);
		
		Connection conn		=	null;
		Statement sqlStmt	=	null;
		ResultSet	results	=	null;
		
		//	Execute the query
		try  {
			conn   = HibernateUtil.getConnection();
			sqlStmt = conn.createStatement();
			results = sqlStmt.executeQuery(sql.toString());
			results.first();
			int			count	= results.getBigDecimal("entry_count").intValue();
			results.close();
			sqlStmt.close();
			conn.close();
			
			return count;
		} catch (SQLException sqlExc) {
			System.out.println(sql);
			System.out.println(sqlExc.getMessage());
			sqlExc.printStackTrace();
			throw sqlExc;
		} finally {
			if (results != null) results.close();
			if (sqlStmt != null) sqlStmt.close();
			if (conn != null) conn.close();
		}
    }

    public static void easyPersist(Object instance)
    {
        HibernateUtil.startTransaction();
        persist(instance);
        HibernateUtil.endTransaction();
    }

    public static void easyDelete(Object instance)
    {
        HibernateUtil.startTransaction();
        delete(instance);
        HibernateUtil.endTransaction();
    }

    public static void easyAttachDirty(Object instance)
    {
        HibernateUtil.startTransaction();
        attachDirty(instance);
        HibernateUtil.endTransaction();
    }

}
