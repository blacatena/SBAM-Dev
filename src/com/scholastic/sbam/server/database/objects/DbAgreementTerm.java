package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.AgreementTerm;
import com.scholastic.sbam.server.database.codegen.AgreementTermId;
import com.scholastic.sbam.server.database.codegen.CancelReason;
import com.scholastic.sbam.server.database.codegen.CommissionType;
import com.scholastic.sbam.server.database.codegen.Product;
import com.scholastic.sbam.server.database.codegen.TermType;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;
import com.scholastic.sbam.shared.objects.CancelReasonInstance;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.objects.ProductInstance;
import com.scholastic.sbam.shared.objects.TermTypeInstance;

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
		instance.setId(dbInstance.getId().getTermId());
		
		instance.setProductCode(dbInstance.getProductCode());
		instance.setStartDate(dbInstance.getStartDate());
		instance.setEndDate(dbInstance.getEndDate());
		instance.setTerminateDate(dbInstance.getTerminateDate());
		instance.setTermTypeCode(dbInstance.getTermType());
		
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
		
		instance.setPrimary(dbInstance.getPrimaryTerm());
		instance.setOrgPath(dbInstance.getOrgPath());
		instance.setPrimaryOrgPath(dbInstance.getPrimaryOrgPath());
		instance.setNote(dbInstance.getNote());
		
		instance.setStatus(dbInstance.getStatus());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		
		return instance;
	}
	
	public static AgreementTerm getById(int agreementId, int termId) {
		AgreementTermId aid = new AgreementTermId();
		aid.setAgreementId(agreementId);
		aid.setTermId(termId);
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
	
	public static void setDescriptions(AgreementTermInstance agreementTerm) {
		if (agreementTerm == null)
			return;
		
		if (agreementTerm.getTermTypeCode() != null) {
			TermType tType = DbTermType.getByCode(agreementTerm.getTermTypeCode());
			if (tType != null) {
				agreementTerm.setTermType(DbTermType.getInstance(tType));
			} else {
				agreementTerm.setTermType(TermTypeInstance.getUnknownInstance(agreementTerm.getTermTypeCode()));
			}
		} else
			agreementTerm.setTermType(TermTypeInstance.getUnknownInstance("none"));
		
		
		if (agreementTerm.getCommissionCode() != null && agreementTerm.getCommissionCode().length() > 0) {
			CommissionType commissionType = DbCommissionType.getByCode(agreementTerm.getCommissionCode());
			if (commissionType != null) {
				agreementTerm.setCommissionType(DbCommissionType.getInstance(commissionType));
			} else {
				agreementTerm.setCommissionType(CommissionTypeInstance.getUnknownInstance(agreementTerm.getCommissionCode()));
			}
		} else
			agreementTerm.setCommissionType(CommissionTypeInstance.getEmptyInstance());
		
		
		if (agreementTerm.getCancelReasonCode() != null && agreementTerm.getCancelReasonCode().length() > 0) {
			CancelReason cancelReason = DbCancelReason.getByCode(agreementTerm.getCancelReasonCode());
			if (cancelReason != null) {
				agreementTerm.setCancelReason(DbCancelReason.getInstance(cancelReason));
			} else {
				agreementTerm.setCancelReason(CancelReasonInstance.getUnknownInstance(agreementTerm.getCancelReasonCode()));
			}
		} else
			agreementTerm.setCancelReason(CancelReasonInstance.getEmptyInstance());
			
		
		if (agreementTerm.getProductCode() != null && agreementTerm.getProductCode().length() > 0) {
			Product product = DbProduct.getByCode(agreementTerm.getProductCode());
			if (product != null) {
				agreementTerm.setProduct(DbProduct.getInstance(product));
			} else {
				agreementTerm.setProduct(ProductInstance.getUnknownInstance(agreementTerm.getProductCode()));
			}
		} else
			agreementTerm.setProduct(ProductInstance.getEmptyInstance());
		
	}
}
