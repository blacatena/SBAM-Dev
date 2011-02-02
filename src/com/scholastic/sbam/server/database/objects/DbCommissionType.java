package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.CommissionType;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbCommissionType extends HibernateAccessor {
	
	static String objectName = CommissionType.class.getSimpleName();
	
	public static CommissionTypeInstance getInstance(CommissionType dbInstance) {
		CommissionTypeInstance instance = new CommissionTypeInstance();
		instance.setCommissionCode(dbInstance.getCommissionCode());
		instance.setDescription(dbInstance.getDescription());
		instance.setShortName(dbInstance.getShortName());
		instance.setProducts(dbInstance.getProducts());
		instance.setSites(dbInstance.getSites());
		instance.setAgreements(dbInstance.getAgreements());
		instance.setAgreementTerms(dbInstance.getAgreementTerms());
		instance.setStatus(dbInstance.getStatus());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		
		return instance;
	}
	
	public static CommissionType getByCode(String code) {
		return (CommissionType) getByField(objectName, "commissionCode", code, "description");
	}
	
	public static CommissionType getByCode(char code) {
		return (CommissionType) getByField(objectName, "commissionCode", code + "", "description");
	}
	
	public static List<CommissionType> findAll() {
		List<Object> results = findAll(objectName);
		List<CommissionType> reasons = new ArrayList<CommissionType>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((CommissionType) results.get(i));
		return reasons;
	}
	
	public static List<CommissionType> findFiltered(String code, String description, char products, char sites, char agreements, char agreementTerms, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (code != null && code.length() > 0)
            	crit.add(Restrictions.like("commissionCode", code));
            if (description != null && description.length() > 0)
            	crit.add(Restrictions.like("description", description));
            if (products != 0)
            	crit.add(Restrictions.like("products", products));
            if (sites != 0)
            	crit.add(Restrictions.like("sites", sites));
            if (agreements != 0)
            	crit.add(Restrictions.like("agreements", agreements));
            if (agreementTerms != 0)
            	crit.add(Restrictions.like("agreementTerms", agreementTerms));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("description"));
            @SuppressWarnings("unchecked")
			List<CommissionType> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<CommissionType>();
	}
}
