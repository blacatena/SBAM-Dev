package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.CommissionType;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.codegen.SiteId;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.server.fastSearch.InstitutionCache.InstitutionCacheConflict;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.SiteInstance;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbSite extends HibernateAccessor {
	
	static String objectName = Site.class.getSimpleName();
	
	public static SiteInstance getInstance(Site dbInstance) {
		SiteInstance instance = new SiteInstance();

		instance.setUcn(dbInstance.getId().getUcn());
		instance.setUcnSuffix(dbInstance.getId().getUcnSuffix());
		instance.setSiteLocCode(dbInstance.getId().getSiteLocCode());
		
		instance.setDescription(dbInstance.getDescription());
		instance.setPseudoSite(dbInstance.getPseudoSite());
		
		instance.setCommissionCode(dbInstance.getCommissionCode());
		
		instance.setNote(dbInstance.getNote());
		
		instance.setStatus(dbInstance.getStatus());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		
		return instance;
	}
	
	public static Site getById(int ucn, int ucnSuffix, String siteLocCode) {
		SiteId sid = new SiteId();
		sid.setUcn(ucn);
		sid.setUcnSuffix(ucnSuffix);
		sid.setSiteLocCode(siteLocCode);
		try {
			Site instance = (Site) sessionFactory.getCurrentSession().get(getObjectReference(objectName), sid);
			return instance;
		} catch (RuntimeException re) {
        	re.printStackTrace();
            System.out.println(re.getMessage());
			throw re;
		}
	}
	
	public static List<Site> findAll() {
		List<Object> results = findAll(objectName);
		List<Site> reasons = new ArrayList<Site>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((Site) results.get(i));
		return reasons;
	}
	
	public static List<Site> findByUcn(int ucn, int ucnSuffix, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (ucn > 0)
            	crit.add(Restrictions.eq("id.ucn", ucn));
            if (ucnSuffix > 0)
            	crit.add(Restrictions.eq("id.ucnSuffix", ucnSuffix));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("id.ucn"));
            crit.addOrder(Order.asc("id.ucnSuffix"));
            crit.addOrder(Order.asc("description"));
            @SuppressWarnings("unchecked")
			List<Site> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<Site>();
	}
	
	public static void setDescriptions(SiteInstance site) {
		setDescriptions(site, null);
	}
	
	public static void setDescriptions(SiteInstance site, InstitutionInstance institution) {
		if (site == null)
			return;
		
		if (site.getUcn() > 0) {
			if (institution != null && site.getUcn() == institution.getUcn()) {
				site.setInstitution(institution);
			} else {
				Institution dbInstitution = DbInstitution.getByCode(site.getUcn());
				if (dbInstitution != null) {
					site.setInstitution( DbInstitution.getInstance(dbInstitution) );
				} else
					site.setInstitution( InstitutionInstance.getUnknownInstance( site.getUcn()) );
			}
		} else {
			site.setInstitution( InstitutionInstance.getEmptyInstance());
		}
		
		try {
			if (InstitutionCache.getSingleton() != null)
				InstitutionCache.getSingleton().setDescriptions(site.getInstitution());
		} catch (InstitutionCacheConflict e) {
			e.printStackTrace();
		}
		
		if (site.getCommissionCode() != null && site.getCommissionCode().length() > 0) {
			CommissionType commissionType = DbCommissionType.getByCode(site.getCommissionCode());
			if (commissionType != null) {
				site.setCommissionType(DbCommissionType.getInstance(commissionType));
			} else {
				site.setCommissionType(CommissionTypeInstance.getUnknownInstance(site.getCommissionCode()));
			}
		} else
			site.setCommissionType(CommissionTypeInstance.getEmptyInstance());
	}
}
