package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.AgreementContact;
import com.scholastic.sbam.server.database.codegen.AgreementContactId;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.AgreementContactInstance;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbAgreementContact extends HibernateAccessor {
	
	static String objectName = AgreementContact.class.getSimpleName();
	
	public static AgreementContactInstance getInstance(AgreementContact dbInstance) {
		AgreementContactInstance instance = new AgreementContactInstance();

		instance.setAgreementId(dbInstance.getId().getAgreementId());
		instance.setContactId(dbInstance.getId().getContactId());
		
		instance.setRenewalContact(dbInstance.getRenewalContact());
		
		instance.setStatus(dbInstance.getStatus());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		
		return instance;
	}
	
	public static AgreementContact getById(int agreementId, int contactId) {
		AgreementContactId asid = new AgreementContactId();
		asid.setAgreementId(agreementId);
		asid.setContactId(contactId);
		try {
			AgreementContact instance = (AgreementContact) sessionFactory.getCurrentSession().get(getObjectReference(objectName), asid);
			return instance;
		} catch (RuntimeException re) {
        	re.printStackTrace();
            System.out.println(re.getMessage());
			throw re;
		}
	}
	
	public static List<AgreementContact> findAll() {
		List<Object> results = findAll(objectName);
		List<AgreementContact> reasons = new ArrayList<AgreementContact>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((AgreementContact) results.get(i));
		return reasons;
	}
	
	public static List<AgreementContact> findByAgreementId(int agreementId, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (agreementId > 0)
            	crit.add(Restrictions.eq("id.agreementId", agreementId));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("id.contactId"));
            @SuppressWarnings("unchecked")
			List<AgreementContact> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<AgreementContact>();
	}
}
