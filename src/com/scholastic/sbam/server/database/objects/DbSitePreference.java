package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.SitePreference;
import com.scholastic.sbam.server.database.codegen.SitePreferenceId;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.SitePreferenceInstance;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbSitePreference extends HibernateAccessor {
	
	static String objectName = SitePreference.class.getSimpleName();
	
	public static SitePreferenceInstance getInstance(SitePreference dbInstance) {
		SitePreferenceInstance instance = new SitePreferenceInstance();

		instance.setUcn(dbInstance.getId().getUcn());
		instance.setUcnSuffix(dbInstance.getId().getUcnSuffix());
		instance.setSiteLocCode(dbInstance.getId().getSiteLocCode());
		instance.setPrefCatCode(dbInstance.getId().getPrefCatCode());
		instance.setPrefSelCode(dbInstance.getPrefSelCode());
		
		instance.setStatus(dbInstance.getStatus());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		
		return instance;
	}
	
	public static SitePreference getById(int ucn, int ucnSuffix, String siteLocCode, String prefCatCode) {
		SitePreferenceId scid = new SitePreferenceId();
		scid.setUcn(ucn);
		scid.setUcnSuffix(ucnSuffix);
		scid.setSiteLocCode(siteLocCode);
		scid.setPrefCatCode(prefCatCode);
		try {
			SitePreference instance = (SitePreference) sessionFactory.getCurrentSession().get(getObjectReference(objectName), scid);
			return instance;
		} catch (RuntimeException re) {
        	re.printStackTrace();
            System.out.println(re.getMessage());
			throw re;
		}
	}
	
	public static List<SitePreference> findAll() {
		List<Object> results = findAll(objectName);
		List<SitePreference> reasons = new ArrayList<SitePreference>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((SitePreference) results.get(i));
		return reasons;
	}
	
	public static List<SitePreference> findBySite(int ucn, int ucnSuffix, String siteLocCode, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (ucn > 0)
            	crit.add(Restrictions.eq("id.ucn", ucn));
            if (ucnSuffix > 0)
            	crit.add(Restrictions.eq("id.ucnSuffix", ucnSuffix));
            if (siteLocCode != null)
            	crit.add(Restrictions.eq("id.siteLocCode", siteLocCode));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("id.prefCatCode"));
            @SuppressWarnings("unchecked")
			List<SitePreference> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<SitePreference>();
	}
}
