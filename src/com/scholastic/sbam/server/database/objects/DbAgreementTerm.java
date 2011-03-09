package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.AgreementTerm;
import com.scholastic.sbam.server.database.codegen.AgreementTermId;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbAgreementTerm extends HibernateAccessor {
	
	static String objectName = AgreementTerm.class.getSimpleName();
	
	public static AgreementTermInstance getInstance(AgreementTerm dbInstance) {
		AgreementTermInstance instance = new AgreementTermInstance();

		instance.setAgreementId(dbInstance.getId().getAgreementId());
		instance.setId(dbInstance.getId().getId());
		
		instance.setProductCode(dbInstance.getProductCode());
		instance.setStartDate(dbInstance.getStartDate());
		instance.setEndDate(dbInstance.getEndDate());
		instance.setTerminateDate(dbInstance.getTerminateDate());
		instance.setTermType(dbInstance.getTermType());
		
		instance.setCommissionCode(dbInstance.getCommissionCode());
		instance.setCancelReasonCode(dbInstance.getCancelReasonCode());
		instance.setCancelDate(dbInstance.getCancelDate());
		
		instance.setDollarValue(dbInstance.getDollarValue().doubleValue());
		instance.setWorkstations(dbInstance.getWorkstations());
		instance.setBuildings(dbInstance.getBuildings());
		instance.setPopulation(dbInstance.getPopulation());
		instance.setEnrollment(dbInstance.getEnrollment());
		
		instance.setPoNumber(dbInstance.getPoNumber());
		instance.setReferenceSaId(dbInstance.getReferenceSaId());
		
		instance.setPrimary(dbInstance.getPrimary());
		instance.setOrgPath(dbInstance.getOrgPath());
		instance.setPrimaryOrgPath(dbInstance.getPrimaryOrgPath());
		instance.setNote(dbInstance.getNote());
		
		instance.setStatus(dbInstance.getStatus());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		
		return instance;
	}
	
	public static AgreementTerm getById(int agreementId, int id) {
		AgreementTermId aid = new AgreementTermId();
		aid.setAgreementId(agreementId);
		aid.setId(id);
		try {
			AgreementTerm instance = (AgreementTerm) sessionFactory.getCurrentSession().get(getObjectReference(objectName), aid);
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
	
	public static List<AgreementTerm> findAll() {
		List<Object> results = findAll(objectName);
		List<AgreementTerm> reasons = new ArrayList<AgreementTerm>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((AgreementTerm) results.get(i));
		return reasons;
	}
	
	public static List<AgreementTerm> findByAgreementId(int agreementId, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (agreementId > 0)
            	crit.add(Restrictions.eq("id.agreementId", agreementId));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("orgPath"));
            crit.addOrder(Order.asc("endDate"));
            @SuppressWarnings("unchecked")
			List<AgreementTerm> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<AgreementTerm>();
	}
}
