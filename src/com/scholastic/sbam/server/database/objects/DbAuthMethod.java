package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.AuthMethod;
import com.scholastic.sbam.server.database.codegen.AuthMethodId;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;
import com.scholastic.sbam.shared.objects.SiteInstance;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbAuthMethod extends HibernateAccessor {
	
	static String objectName = AuthMethod.class.getSimpleName();
	
	public static AuthMethodInstance getInstance(AuthMethod dbInstance) {
		AuthMethodInstance instance = new AuthMethodInstance();

		instance.setAgreementId(dbInstance.getId().getAgreementId());
		instance.setUcn(dbInstance.getId().getUcn());
		instance.setUcnSuffix(dbInstance.getId().getUcnSuffix());
		instance.setSiteLocCode(dbInstance.getId().getSiteLocCode());

		instance.setMethodType(dbInstance.getId().getMethodType());
		instance.setMethodKey(dbInstance.getId().getMethodKey());
		
		instance.setIpLo(dbInstance.getIpLo());
		instance.setIpHi(dbInstance.getIpHi());
		instance.setUrl(dbInstance.getUrl());
		instance.setUserType(dbInstance.getUserType());
		instance.setUserId(dbInstance.getUserId());
		instance.setPassword(dbInstance.getPassword());
		
		instance.setProxyId(dbInstance.getProxyId());
		
		instance.setApproved(dbInstance.getApproved());
		instance.setActivated(dbInstance.getActivated());
		instance.setValidated(dbInstance.getValidated());
		instance.setRemote(dbInstance.getRemote());
		
		instance.setNote(dbInstance.getNote());
		instance.setOrgPath(dbInstance.getOrgPath());
		
		instance.setActivatedDatetime(dbInstance.getActivatedDatetime());
		instance.setDeactivatedDatetime(dbInstance.getDeactivatedDatetime());
		instance.setReactivatedDatetime(dbInstance.getReactivatedDatetime());
		
		instance.setStatus(dbInstance.getStatus());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		instance.setUpdatedDatetime(dbInstance.getUpdatedDatetime());
		
		return instance;
	}
	
	public static AuthMethod getById(int agreementId, int ucn, int ucnSuffix, String siteLocCode, String methodType, String methodKey) {
		AuthMethodId amid = new AuthMethodId();
		amid.setAgreementId(agreementId);
		amid.setUcn(ucn);
		amid.setUcnSuffix(ucnSuffix);
		amid.setSiteLocCode(siteLocCode);
		amid.setMethodType(methodType);
		amid.setMethodKey(methodKey);
		try {
			AuthMethod instance = (AuthMethod) sessionFactory.getCurrentSession().get(getObjectReference(objectName), amid);
			return instance;
		} catch (RuntimeException re) {
        	re.printStackTrace();
            System.out.println(re.getMessage());
			throw re;
		}
	}
	
	public static List<AuthMethod> findAll() {
		List<Object> results = findAll(objectName);
		List<AuthMethod> reasons = new ArrayList<AuthMethod>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((AuthMethod) results.get(i));
		return reasons;
	}
	
	public static List<AuthMethod> findByAgreementId(int agreementId, String methodType, char status, char neStatus) {
		return findByOwner(agreementId, 0, 0, null, methodType, status, neStatus);
	}
	
	public static List<AuthMethod> findBySite( int ucn, int ucnSuffix, String siteLocCode, String methodType, char status, char neStatus) {
		return findByOwner(0, ucn, ucnSuffix, siteLocCode, methodType, status, neStatus);
	}
	
	public static List<AuthMethod> findByOwner(int agreementId, int ucn, int ucnSuffix, String siteLocCode, String methodType, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (agreementId > 0)
            	crit.add(Restrictions.eq("id.agreementId", agreementId));
            if (ucn > 0)
            	crit.add(Restrictions.eq("id.ucn", ucn));
            if (ucnSuffix > 0)
            	crit.add(Restrictions.eq("id.ucnSuffix", ucnSuffix));
            if (siteLocCode != null)
            	crit.add(Restrictions.eq("id.siteLocCode", siteLocCode));
            if (methodType != null)
            	crit.add(Restrictions.eq("id.methodType", methodType));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("id.agreementId"));
            crit.addOrder(Order.asc("id.ucn"));
            crit.addOrder(Order.asc("id.ucnSuffix"));
            crit.addOrder(Order.asc("id.siteLocCode"));
            @SuppressWarnings("unchecked")
			List<AuthMethod> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<AuthMethod>();
	}
	
	public static void setDescriptions(AuthMethodInstance remoteUrl) {
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
