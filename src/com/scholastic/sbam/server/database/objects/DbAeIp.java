package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.AeIp;
import com.scholastic.sbam.server.database.codegen.AeIpId;
import com.scholastic.sbam.server.database.util.HibernateAccessor;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbAeIp extends HibernateAccessor {
	
	static String objectName = AeIp.class.getSimpleName();
	
	public static AeIp getById(int aeId, int auId, String ip) {
		AeIpId aeIpId = new AeIpId();
		aeIpId.setAeId(aeId);
		aeIpId.setAuId(auId);
		aeIpId.setIp(ip);
		return getById(aeIpId);
	}

	public static AeIp getById(AeIpId aeIpId) {
		try {
			AeIp instance = (AeIp) sessionFactory.getCurrentSession().get(getObjectReference(objectName), aeIpId);
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
	
	public static List<AeIp> findAll() {
		List<Object> results = findAll(objectName);
		List<AeIp> reasons = new ArrayList<AeIp>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((AeIp) results.get(i));
		return reasons;
	}
	
	public static List<AeIp> findByAeId(int aeId) {
		return findByAuId(aeId, -1);
	}
	
	public static List<AeIp> findByAuId(int aeId, int auId) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            
            if (aeId > 0)
            	crit.add(Restrictions.eq("id.aeId", aeId));   
            if (auId > 0)
            	crit.add(Restrictions.eq("id.auId", auId)); 
            
            crit.addOrder(Order.asc("id.auId"));
            crit.addOrder(Order.asc("ipLo"));
            crit.addOrder(Order.asc("ipHi"));
            @SuppressWarnings("unchecked")
			List<AeIp> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
	}
	
	public static List<AeIp> findByIpRangeCode(int aeId, String ipRangeCode) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            
            if (aeId > 0)
            	crit.add(Restrictions.eq("id.aeId", aeId));
            if (ipRangeCode != null)
            	crit.add(Restrictions.like("ipRangeCode", ipRangeCode + "%")); 
            
            crit.addOrder(Order.asc("id.auId"));
            crit.addOrder(Order.asc("ipLo"));
            crit.addOrder(Order.asc("ipHi"));
            @SuppressWarnings("unchecked")
			List<AeIp> objects = crit.list();
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
