package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.AeUid;
import com.scholastic.sbam.server.database.codegen.AeUidId;
import com.scholastic.sbam.server.database.util.HibernateAccessor;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbAeUid extends HibernateAccessor {
	
	static String objectName = AeUid.class.getSimpleName();
	
	public static AeUid getById(int aeId, int auId, String userId) {
		AeUidId aeUidId = new AeUidId();
		aeUidId.setAeId(aeId);
		aeUidId.setAuId(auId);
		aeUidId.setUserId(userId);
		
		return getById(aeUidId);
	}
	
	public static AeUid getById(AeUidId aeUidId) {

		try {
			AeUid instance = (AeUid) sessionFactory.getCurrentSession().get(getObjectReference(objectName), aeUidId);
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
	
	public static List<AeUid> findAll() {
		List<Object> results = findAll(objectName);
		List<AeUid> reasons = new ArrayList<AeUid>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((AeUid) results.get(i));
		return reasons;
	}
	
	public static List<AeUid> findByAuId(int aeId, int auId) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            
            if (aeId > 0)
            	crit.add(Restrictions.eq("id.aeId", aeId));   
            if (auId > 0)
            	crit.add(Restrictions.eq("id.auId", auId)); 
            
            crit.addOrder(Order.desc("id.auId"));
            @SuppressWarnings("unchecked")
			List<AeUid> objects = crit.list();
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
