package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.RemoteUrl;
import com.scholastic.sbam.server.database.codegen.RemoteUrlId;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.RemoteUrlInstance;
import com.scholastic.sbam.shared.objects.SiteInstance;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbRemoteUrl extends HibernateAccessor {
	
	static String objectName = RemoteUrl.class.getSimpleName();
	
	public static RemoteUrlInstance getInstance(RemoteUrl dbInstance) {
		RemoteUrlInstance instance = new RemoteUrlInstance();

		instance.setUcn(dbInstance.getId().getUcn());
		instance.setUcnSuffix(dbInstance.getId().getUcnSuffix());
		instance.setSiteLocCode(dbInstance.getId().getSiteLocCode());
		
		instance.setUrl(dbInstance.getId().getUrl());
		
		instance.setNote(dbInstance.getNote());
		instance.setOrgPath(dbInstance.getOrgPath());
		
		instance.setApproved(dbInstance.getApproved());
		instance.setActivated(dbInstance.getActivated());
		
		instance.setStatus(dbInstance.getStatus());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		
		return instance;
	}
	
	public static RemoteUrl getById(int agreementId, int ucn, int ucnSuffix, String siteLocCode, String url) {
		RemoteUrlId ruid = new RemoteUrlId();
		ruid.setAgreementId(agreementId);
		ruid.setUcn(ucn);
		ruid.setUcnSuffix(ucnSuffix);
		ruid.setSiteLocCode(siteLocCode);
		ruid.setUrl(url);
		try {
			RemoteUrl instance = (RemoteUrl) sessionFactory.getCurrentSession().get(getObjectReference(objectName), ruid);
			return instance;
		} catch (RuntimeException re) {
        	re.printStackTrace();
            System.out.println(re.getMessage());
			throw re;
		}
	}
	
	public static List<RemoteUrl> findAll() {
		List<Object> results = findAll(objectName);
		List<RemoteUrl> reasons = new ArrayList<RemoteUrl>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((RemoteUrl) results.get(i));
		return reasons;
	}
	
	public static List<RemoteUrl> findByAgreementId(int agreementId, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (agreementId > 0)
            	crit.add(Restrictions.eq("id.agreementId", agreementId));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("id.ucn"));
            crit.addOrder(Order.asc("id.ucnSuffix"));
            crit.addOrder(Order.asc("id.siteLocCode"));
            @SuppressWarnings("unchecked")
			List<RemoteUrl> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<RemoteUrl>();
	}
	
	public static void setDescriptions(RemoteUrlInstance remoteUrl) {
		if (remoteUrl == null)
			return;
		
		if (remoteUrl.getUcn() > 0) {
			if (remoteUrl.getSiteLocCode() != null && remoteUrl.getSiteLocCode().length() > 0) {
				Site dbSite = DbSite.getById(remoteUrl.getUcn(), remoteUrl.getUcnSuffix(), remoteUrl.getSiteLocCode());
				if (dbSite != null)
					remoteUrl.setSite( DbSite.getInstance(dbSite) );
				else
					remoteUrl.setSite( SiteInstance.getUnknownInstance( remoteUrl.getUcn(), remoteUrl.getUcnSuffix(), remoteUrl.getSiteLocCode()) );
			} else {
				remoteUrl.setSite( SiteInstance.getAllInstance(remoteUrl.getUcn(), remoteUrl.getUcnSuffix()) );
			}
			Institution dbInstitution = DbInstitution.getByCode(remoteUrl.getUcn());
			remoteUrl.getSite().setInstitution( DbInstitution.getInstance( dbInstitution));
		} else {
			remoteUrl.setSite( SiteInstance.getEmptyInstance());
		}
	}
}
