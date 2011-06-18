package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.AePrefCode;
import com.scholastic.sbam.server.database.codegen.AePrefCodeId;
import com.scholastic.sbam.server.database.util.HibernateAccessor;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbAePrefCode extends HibernateAccessor {
	
	static String objectName = AePrefCode.class.getSimpleName();
	
	public static AePrefCode getById(int aeId, String prefCode) {
		AePrefCodeId aePrefCodeId = new AePrefCodeId();
		aePrefCodeId.setAeId(aeId);
		aePrefCodeId.setPrefCode(prefCode);
		return getById(aePrefCodeId);
	}
	
	public static AePrefCode getById(AePrefCodeId aePrefCodeId) {
		try {
			AePrefCode instance = (AePrefCode) sessionFactory.getCurrentSession().get(getObjectReference(objectName), aePrefCodeId);
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
	
	public static List<AePrefCode> findAll() {
		List<Object> results = findAll(objectName);
		List<AePrefCode> reasons = new ArrayList<AePrefCode>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((AePrefCode) results.get(i));
		return reasons;
	}
	
	public static List<AePrefCode> findByAeId(int aeId) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            
            if (aeId > 0)
            	crit.add(Restrictions.eq("id.aeId", aeId));  
            
            crit.addOrder(Order.asc("id.aeId"));
            crit.addOrder(Order.asc("id.prefCode"));
            @SuppressWarnings("unchecked")
			List<AePrefCode> objects = crit.list();
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
