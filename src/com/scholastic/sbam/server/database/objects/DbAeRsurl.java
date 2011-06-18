package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.AeRsurl;
import com.scholastic.sbam.server.database.codegen.AeRsurlId;
import com.scholastic.sbam.server.database.util.HibernateAccessor;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbAeRsurl extends HibernateAccessor {
	
	static String objectName = AeRsurl.class.getSimpleName();
	
	public static AeRsurl getById(int aeId, int auId, String url) {
		AeRsurlId aeRsurlId = new AeRsurlId();
		aeRsurlId.setAeId(aeId);
		aeRsurlId.setAuId(auId);
		aeRsurlId.setUrl(url);
		return getById(aeRsurlId);
	}
	
	public static AeRsurl getById(AeRsurlId aeRsurlId) {
		try {
			AeRsurl instance = (AeRsurl) sessionFactory.getCurrentSession().get(getObjectReference(objectName), aeRsurlId);
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
	
	public static List<AeRsurl> findAll() {
		List<Object> results = findAll(objectName);
		List<AeRsurl> reasons = new ArrayList<AeRsurl>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((AeRsurl) results.get(i));
		return reasons;
	}
	
	public static List<AeRsurl> findByAeId(int aeId) {
		return findByAuId(aeId, -1);
	}
	
	public static List<AeRsurl> findByAuId(int aeId, int auId) {
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
			List<AeRsurl> objects = crit.list();
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
