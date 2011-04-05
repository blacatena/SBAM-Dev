package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.AgreementSite;
import com.scholastic.sbam.server.database.codegen.AgreementSiteId;
import com.scholastic.sbam.server.database.codegen.CancelReason;
import com.scholastic.sbam.server.database.codegen.CommissionType;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.AgreementSiteInstance;
import com.scholastic.sbam.shared.objects.CancelReasonInstance;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.objects.SiteInstance;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbAgreementSite extends HibernateAccessor {
	
	static String objectName = AgreementSite.class.getSimpleName();
	
	public static AgreementSiteInstance getInstance(AgreementSite dbInstance) {
		AgreementSiteInstance instance = new AgreementSiteInstance();

		instance.setAgreementId(dbInstance.getId().getAgreementId());
		instance.setSiteUcn(dbInstance.getId().getSiteUcn());
		instance.setSiteUcnSuffix(dbInstance.getId().getSiteUcnSuffix());
		instance.setSiteLocCode(dbInstance.getId().getSiteLocCode());
		
		instance.setCommissionCode(dbInstance.getCommissionCode());
		
		instance.setCancelReasonCode(dbInstance.getCancelReasonCode());
		instance.setActiveDate(dbInstance.getActiveDate());
		instance.setInactiveDate(dbInstance.getInactiveDate());
		
		instance.setOrgPath(dbInstance.getOrgPath());
		instance.setNote(dbInstance.getNote());
		
		instance.setStatus(dbInstance.getStatus());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		
		return instance;
	}
	
	public static AgreementSite getById(int agreementId, int siteUcn, int siteUcnSuffix, String siteLocCode) {
		AgreementSiteId asid = new AgreementSiteId();
		asid.setAgreementId(agreementId);
		asid.setSiteUcn(siteUcn);
		asid.setSiteUcnSuffix(siteUcnSuffix);
		asid.setSiteLocCode(siteLocCode);
		try {
			AgreementSite instance = (AgreementSite) sessionFactory.getCurrentSession().get(getObjectReference(objectName), asid);
			return instance;
		} catch (RuntimeException re) {
        	re.printStackTrace();
            System.out.println(re.getMessage());
			throw re;
		}
	}
	
	public static List<AgreementSite> findAll() {
		List<Object> results = findAll(objectName);
		List<AgreementSite> reasons = new ArrayList<AgreementSite>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((AgreementSite) results.get(i));
		return reasons;
	}
	
	public static List<AgreementSite> findByAgreementId(int agreementId, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (agreementId > 0)
            	crit.add(Restrictions.eq("id.agreementId", agreementId));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("id.siteUcn"));
            crit.addOrder(Order.asc("id.siteUcnSuffix"));
            crit.addOrder(Order.asc("id.siteLocCode"));
            @SuppressWarnings("unchecked")
			List<AgreementSite> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<AgreementSite>();
	}
	
	public static void setDescriptions(AgreementSiteInstance agreementSite) {
		if (agreementSite == null)
			return;
		
		if (agreementSite.getSiteUcn() > 0) {
			if (agreementSite.getSiteLocCode() != null && agreementSite.getSiteLocCode().length() > 0) {
	//			Institution dbInstitution = DbInstitution.getByCode(agreementSite.getSiteUcn());
	//			if (dbInstitution != null)
	//				agreementSite.setInstitution( DbInstitution.getInstance(dbInstitution) );
	//			else
	//				agreementSite.setInstitution( InstitutionInstance.getUnknownInstance( agreementSite.getSiteUcn()) );
				Site dbSite = DbSite.getById(agreementSite.getSiteUcn(), agreementSite.getSiteUcnSuffix(), agreementSite.getSiteLocCode());
				if (dbSite != null)
					agreementSite.setSite( DbSite.getInstance(dbSite) );
				else
					agreementSite.setSite( SiteInstance.getUnknownInstance(agreementSite.getSiteUcn(), agreementSite.getSiteUcnSuffix(), agreementSite.getSiteLocCode()));
			} else {
				agreementSite.setSite(SiteInstance.getAllInstance(agreementSite.getSiteUcn(), agreementSite.getSiteUcnSuffix()));
			}
			DbSite.setDescriptions(agreementSite.getSite());
		} else {
//			agreementSite.setInstitution( InstitutionInstance.getEmptyInstance());
			agreementSite.setSite(SiteInstance.getEmptyInstance());
		}
		
		if (agreementSite.getCommissionCode() != null && agreementSite.getCommissionCode().length() > 0) {
			CommissionType commissionType = DbCommissionType.getByCode(agreementSite.getCommissionCode());
			if (commissionType != null) {
				agreementSite.setCommissionType(DbCommissionType.getInstance(commissionType));
			} else {
				agreementSite.setCommissionType(CommissionTypeInstance.getUnknownInstance(agreementSite.getCommissionCode()));
			}
		} else
			agreementSite.setCommissionType(CommissionTypeInstance.getEmptyInstance());
		
		
		if (agreementSite.getCancelReasonCode() != null && agreementSite.getCancelReasonCode().length() > 0) {
			CancelReason cancelReason = DbCancelReason.getByCode(agreementSite.getCancelReasonCode());
			if (cancelReason != null) {
				agreementSite.setCancelReason(DbCancelReason.getInstance(cancelReason));
			} else {
				agreementSite.setCancelReason(CancelReasonInstance.getUnknownInstance(agreementSite.getCancelReasonCode()));
			}
		} else
			agreementSite.setCancelReason(CancelReasonInstance.getEmptyInstance());
	}
}
