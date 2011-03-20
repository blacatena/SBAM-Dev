package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.CancelReason;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.CancelReasonInstance;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbCancelReason extends HibernateAccessor {
	
	static String objectName = CancelReason.class.getSimpleName();
	
	public static CancelReasonInstance getInstance(CancelReason dbInstance) {
		CancelReasonInstance instance = new CancelReasonInstance();
		instance.setCancelReasonCode(dbInstance.getCancelReasonCode());
		instance.setDescription(dbInstance.getDescription());
		instance.setChangeNotCancel(dbInstance.getChangeNotCancel());
		instance.setStatus(dbInstance.getStatus());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		
		return instance;
	}
	
	public static CancelReason getByCode(String code) {
		return (CancelReason) getByField(objectName, "cancelReasonCode", code, "description");
	}
	
	public static List<CancelReason> findAll() {
		List<Object> results = findAll(objectName);
		List<CancelReason> reasons = new ArrayList<CancelReason>();
		for (int i = 0; i < results.size(); i++)
			reasons.add((CancelReason) results.get(i));
		return reasons;
	}
	
	public static List<CancelReason> findFiltered(String code, String description, char changeNotCancel, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (code != null && code.length() > 0)
            	crit.add(Restrictions.like("cancelReasonCode", code));
            if (description != null && description.length() > 0)
            	crit.add(Restrictions.like("description", description));
            if (changeNotCancel != 0)
            	crit.add(Restrictions.like("changeNotCancel", changeNotCancel));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("description"));
            @SuppressWarnings("unchecked")
			List<CancelReason> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<CancelReason>();
	}
}
