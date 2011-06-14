package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.AePref;
import com.scholastic.sbam.server.database.codegen.AePrefId;
import com.scholastic.sbam.server.database.util.HibernateAccessor;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbAePref extends HibernateAccessor {
	
	static String objectName = AePref.class.getSimpleName();
	
	public static AePref getById(int aeId, int auId, String prefCode) {
		AePrefId aePrefId = new AePrefId();
		aePrefId.setAeId(aeId);
		aePrefId.setAuId(auId);
		aePrefId.setPrefCode(prefCode);

		try {
			AePref instance = (AePref) sessionFactory.getCurrentSession().get(getObjectReference(objectName), aePrefId);
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
	
	public static List<AePref> findAll() {
		List<Object> results = findAll(objectName);
		List<AePref> reasons = new ArrayList<AePref>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((AePref) results.get(i));
		return reasons;
	}
	
	public static List<AePref> findByAuId(int aeId, int auId) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            
            if (aeId > 0)
            	crit.add(Restrictions.eq("id.aeId", aeId));   
            if (auId > 0)
            	crit.add(Restrictions.eq("id.auId", auId)); 
            
            crit.addOrder(Order.desc("id.auId"));
            @SuppressWarnings("unchecked")
			List<AePref> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
	}
	
	public static List<AePref> findProductServices(int aeId, int auId) {
		try
        { 	
        	String sqlQuery = "SELECT ae_pref.* FROM ae_pref, service WHERE ";
            sqlQuery += " ae_pref.ae_id = " + aeId;
            sqlQuery += " AND ae_pref.au_id = " + auId;
            sqlQuery += " AND ae_pref.pref_code = service.service_code ";
            sqlQuery += " order by ae_pref.pref_code";
            
            SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
            
            query.addEntity(getObjectReference(objectName));
            
            @SuppressWarnings("unchecked")
			List<AePref> objects = query.list();
            
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<AePref>();
	}
}
