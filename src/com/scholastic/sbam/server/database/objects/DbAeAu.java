package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.AeAu;
import com.scholastic.sbam.server.database.codegen.AeAuId;
import com.scholastic.sbam.server.database.util.HibernateAccessor;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbAeAu extends HibernateAccessor {
	
	static String objectName = AeAu.class.getSimpleName();
	
	public static AeAu getById(int aeId, int auId) {
		AeAuId aeIpId = new AeAuId();
		aeIpId.setAeId(aeId);
		aeIpId.setAuId(auId);
		return getById(aeIpId);
	}

	public static AeAu getById(AeAuId aeIpId) {
		try {
			AeAu instance = (AeAu) sessionFactory.getCurrentSession().get(getObjectReference(objectName), aeIpId);
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
	
	public static List<AeAu> findAll() {
		List<Object> results = findAll(objectName);
		List<AeAu> reasons = new ArrayList<AeAu>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((AeAu) results.get(i));
		return reasons;
	}
	
	public static List<AeAu> findByAeId(int aeId) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            
            if (aeId > 0)
            	crit.add(Restrictions.eq("id.aeId", aeId)); 
            
            crit.addOrder(Order.asc("id.auId"));
            @SuppressWarnings("unchecked")
			List<AeAu> objects = crit.list();
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
