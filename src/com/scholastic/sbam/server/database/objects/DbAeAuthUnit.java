package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.AeAuthUnit;
import com.scholastic.sbam.server.database.util.HibernateAccessor;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbAeAuthUnit extends HibernateAccessor {
	
	static String objectName = AeAuthUnit.class.getSimpleName();
	
	public static AeAuthUnit getByCode(int auId) {
		return (AeAuthUnit) getByField(objectName, "auId", auId, "siteUcn");
	}
	
	public static List<AeAuthUnit> findAll() {
		List<Object> results = findAll(objectName);
		List<AeAuthUnit> reasons = new ArrayList<AeAuthUnit>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((AeAuthUnit) results.get(i));
		return reasons;
	}
	
	public static List<AeAuthUnit> findBySite(int siteUcn, int siteUcnSuffix, String siteLocCode, int billUcn, int billUcnSuffix, int siteParentUcn, int siteParentUcnSuffix) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            
            if (siteUcn > 0)
            	crit.add(Restrictions.eq("siteUcn", siteUcn));   
            if (siteUcnSuffix > 0)
            	crit.add(Restrictions.eq("siteUcnSuffix", siteUcnSuffix));   
            if (siteLocCode != null)
            	crit.add(Restrictions.eq("siteLocCode", siteLocCode));   
            if (billUcn > 0)
            	crit.add(Restrictions.eq("billUcn", billUcn));   
            if (billUcnSuffix > 0)
            	crit.add(Restrictions.eq("siteParentUcnSuffix", siteParentUcnSuffix));   
            if (siteParentUcn > 0)
            	crit.add(Restrictions.eq("siteParentUcn", siteParentUcn));   
            if (siteParentUcnSuffix > 0)
            	crit.add(Restrictions.eq("siteParentUcnSuffix", siteParentUcnSuffix));
            
            crit.addOrder(Order.desc("auId"));
            @SuppressWarnings("unchecked")
			List<AeAuthUnit> objects = crit.list();
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
