package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.AeUrl;
import com.scholastic.sbam.server.database.codegen.AeUrlId;
import com.scholastic.sbam.server.database.util.HibernateAccessor;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbAeUrl extends HibernateAccessor {
	
	static String objectName = AeUrl.class.getSimpleName();
	
	public static AeUrl getById(int aeId, int auId, String url) {
		AeUrlId aeUrlId = new AeUrlId();
		aeUrlId.setAeId(aeId);
		aeUrlId.setAuId(auId);
		aeUrlId.setUrl(url);
		
		return getById(aeUrlId);
	}
	
	public static AeUrl getById(AeUrlId aeUrlId) {

		try {
			AeUrl instance = (AeUrl) sessionFactory.getCurrentSession().get(getObjectReference(objectName), aeUrlId);
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
	
	public static List<AeUrl> findAll() {
		List<Object> results = findAll(objectName);
		List<AeUrl> reasons = new ArrayList<AeUrl>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((AeUrl) results.get(i));
		return reasons;
	}
	
	public static List<AeUrl> findByAeId(int aeId) {
		return findByAuId(aeId, -1);
	}
	
	public static List<AeUrl> findByAuId(int aeId, int auId) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            
            if (aeId > 0)
            	crit.add(Restrictions.eq("id.aeId", aeId));   
            if (auId > 0)
            	crit.add(Restrictions.eq("id.auId", auId)); 

            crit.addOrder(Order.asc("id.auId"));
            crit.addOrder(Order.asc("id.url"));
            @SuppressWarnings("unchecked")
			List<AeUrl> objects = crit.list();
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
