package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.AgreementType;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.AgreementTypeInstance;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbAgreementType extends HibernateAccessor {
	
	static String objectName = AgreementType.class.getSimpleName();
	
	public static AgreementTypeInstance getInstance(AgreementType dbInstance) {
		AgreementTypeInstance instance = new AgreementTypeInstance();
		instance.setAgreementTypeCode(dbInstance.getAgreementTypeCode());
		instance.setDescription(dbInstance.getDescription());
		instance.setShortName(dbInstance.getShortName());
		instance.setStatus(dbInstance.getStatus());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		
		return instance;
	}
	
	public static AgreementType getByCode(String code) {
		return (AgreementType) getByField(objectName, "agreementTypeCode", code, "description");
	}
	
	public static AgreementType getByCode(char code) {
		return (AgreementType) getByField(objectName, "agreementTypeCode", code + "", "description");
	}
	
	public static List<AgreementType> findAll() {
		List<Object> results = findAll(objectName);
		List<AgreementType> reasons = new ArrayList<AgreementType>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((AgreementType) results.get(i));
		return reasons;
	}
	
	public static List<AgreementType> findFiltered(String code, String description, String shortName, char activate, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (code != null && code.length() > 0)
            	crit.add(Restrictions.like("agreementTypeCode", code));
            if (description != null && description.length() > 0)
            	crit.add(Restrictions.like("description", description));
            if (shortName != null && shortName.length() > 0)
            	crit.add(Restrictions.like("shortName", description));
            if (activate != 0)
            	crit.add(Restrictions.like("activate", activate));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("description"));
            @SuppressWarnings("unchecked")
			List<AgreementType> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<AgreementType>();
	}
}
