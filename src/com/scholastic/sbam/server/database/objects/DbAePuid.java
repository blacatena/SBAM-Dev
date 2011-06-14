package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.AePuid;
import com.scholastic.sbam.server.database.codegen.AePuidId;
import com.scholastic.sbam.server.database.util.HibernateAccessor;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbAePuid extends HibernateAccessor {
	
	static String objectName = AePuid.class.getSimpleName();
	
	public static AePuid getById(int aeId, int auId, String userId, String ip) {
		AePuidId aePuidId = new AePuidId();
		aePuidId.setAeId(aeId);
		aePuidId.setAuId(auId);
		aePuidId.setUserId(userId);
		aePuidId.setIp(ip);
		
		return getById(aePuidId);
	}
	
	public static AePuid getById(AePuidId aePuidId) {
		try {
			AePuid instance = (AePuid) sessionFactory.getCurrentSession().get(getObjectReference(objectName), aePuidId);
//			if (instance == null) {
//				log.debug("get successful, no instance found");
//			} else {
//				log.debug("get successful, instance found");
//			}
			return instance;
		} catch (RuntimeException re) {
        	re.printStackTrace();
            System.out.println(re.getMessage());
			throw re;
		}
	}
	
	public static List<AePuid> findAll() {
		List<Object> results = findAll(objectName);
		List<AePuid> reasons = new ArrayList<AePuid>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((AePuid) results.get(i));
		return reasons;
	}
	
	public static List<AePuid> findByAuId(int aeId, int auId) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            
            if (aeId > 0)
            	crit.add(Restrictions.eq("id.aeId", aeId));   
            if (auId > 0)
            	crit.add(Restrictions.eq("id.auId", auId)); 
            
            crit.addOrder(Order.desc("id.auId"));
            @SuppressWarnings("unchecked")
			List<AePuid> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
	}
	
	public static List<AePuid> findByAuUid(int aeId, int auId, String userId) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            
            if (aeId > 0)
            	crit.add(Restrictions.eq("id.aeId", aeId));   
            if (auId > 0)
            	crit.add(Restrictions.eq("id.auId", auId));   
            if (userId != null)
            	crit.add(Restrictions.eq("id.userId", userId));
            
            crit.addOrder(Order.desc("id.auId"));
            @SuppressWarnings("unchecked")
			List<AePuid> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
	}
	
	public static List<AePuid> findByUid(int aeId, String userId) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            
            if (aeId > 0)
            	crit.add(Restrictions.eq("id.aeId", aeId));   
            if (userId != null)
            	crit.add(Restrictions.eq("id.userId", userId));
            
            crit.addOrder(Order.desc("id.auId"));
            @SuppressWarnings("unchecked")
			List<AePuid> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
	}
}
