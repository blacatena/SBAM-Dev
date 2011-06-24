package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.AeConflict;
import com.scholastic.sbam.server.database.codegen.AeConflictId;
import com.scholastic.sbam.server.database.util.HibernateAccessor;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbAeConflict extends HibernateAccessor {
	
	static String objectName = AeConflict.class.getSimpleName();
	
	public static AeConflict getById(int aeId, int conflictId) {
		AeConflictId aeConflictId = new AeConflictId();
		aeConflictId.setAeId(aeId);
		aeConflictId.setConflictId(conflictId);
		return getById(aeConflictId);
	}

	public static AeConflict getById(AeConflictId aeConflictId) {
		try {
			AeConflict instance = (AeConflict) sessionFactory.getCurrentSession().get(getObjectReference(objectName), aeConflictId);
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
	
	public static List<AeConflict> findAll() {
		List<Object> results = findAll(objectName);
		List<AeConflict> reasons = new ArrayList<AeConflict>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((AeConflict) results.get(i));
		return reasons;
	}
	
	public static List<AeConflict> findByAeId(int aeId) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            
            if (aeId > 0)
            	crit.add(Restrictions.eq("id.aeId", aeId)); 
            
            crit.addOrder(Order.asc("id.conflictId"));
            @SuppressWarnings("unchecked")
			List<AeConflict> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
	}

	public static int getNextConflictId(int aeId) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
        crit.add(Restrictions.eq("id.aeId", aeId));
        crit.setMaxResults(1);
        crit.addOrder(Order.desc("id.conflictId"));
        @SuppressWarnings("unchecked")
		List<AeConflict> objects = crit.list();
        if (objects == null || objects.size() == 0)
        	return 1;
        return objects.get(0).getId().getConflictId() + 1;
	}
}
