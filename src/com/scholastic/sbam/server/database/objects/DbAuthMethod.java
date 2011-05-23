package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.AuthMethod;
import com.scholastic.sbam.server.database.codegen.AuthMethodId;
import com.scholastic.sbam.server.database.codegen.Proxy;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.ProxyInstance;
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

		instance.setForUcn(dbInstance.getForUcn());
		instance.setForUcnSuffix(dbInstance.getForUcnSuffix());
		instance.setForSiteLocCode(dbInstance.getForSiteLocCode());
		
		instance.setIpRangeCode(dbInstance.getIpRangeCode());	//	This is set before the IPs, so it will not be calculated unless it is missing
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
	
	public static AuthMethod getById(int agreementId, int ucn, int ucnSuffix, String siteLocCode, String methodType, int methodKey) {
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

	public static int getNextAuthMethodKey(int agreementId, int ucn, int ucnSuffix, String siteLocCode, String methodType) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
        crit.add(Restrictions.eq("id.agreementId", 	agreementId));
        crit.add(Restrictions.eq("id.ucn", 			ucn));
        crit.add(Restrictions.eq("id.ucnSuffix", 	ucnSuffix));
        crit.add(Restrictions.eq("id.siteLocCode", 	siteLocCode));
        crit.add(Restrictions.eq("id.methodType", 	methodType));
        crit.setMaxResults(1);
        crit.addOrder(Order.desc("id.methodKey"));
        @SuppressWarnings("unchecked")
		List<AuthMethod> objects = crit.list();
        if (objects == null || objects.size() == 0)
        	return 1;
        return objects.get(0).getId().getMethodKey() + 1;
	}
	
	public static List<AuthMethod> findOverlapIps(long ipLo, long ipHi) {
		String ipRangeCode = AuthMethodInstance.getCommonIpRangeCode(ipLo, ipHi);
		if (ipRangeCode == null || ipRangeCode.length() == 0)
			return new ArrayList<AuthMethod>();
		
		//	By restricting the search by range codes, the database is able to effectively find potential overlaps
		List<String> rangeCodes = new ArrayList<String>();
		for (int i = 1; i < ipRangeCode.length(); i++) {
			rangeCodes.add(ipRangeCode.substring(0,i));
		}
		System.out.println(ipRangeCode);
		System.out.println(rangeCodes);
		
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));

        //	This criterion is for database performance (i.e. prevents a full database scan)
        crit.add(Restrictions.or(Restrictions.in("ipRangeCode", rangeCodes), Restrictions.like("ipRangeCode", ipRangeCode + "%")));
        //	These criteria actually perform the real check
        crit.add(Restrictions.le("ipLo", ipHi));
        crit.add(Restrictions.ge("ipHi", ipLo));
        
        @SuppressWarnings("unchecked")
		List<AuthMethod> objects = crit.list();
        return objects;
	}
	
	public static void setDescriptions(AuthMethodInstance authMethod) {
		if (authMethod == null)
			return;
		
		if (authMethod.getForUcn() > 0) {
			if (authMethod.getForSiteLocCode() != null && authMethod.getForSiteLocCode().length() > 0) {
				Site dbSite = DbSite.getById(authMethod.getForUcn(), authMethod.getForUcnSuffix(), authMethod.getForSiteLocCode());
				if (dbSite != null)
					authMethod.setSite( DbSite.getInstance(dbSite) );
				else
					authMethod.setSite( SiteInstance.getUnknownInstance( authMethod.getForUcn(), authMethod.getForUcnSuffix(), authMethod.getForSiteLocCode()) );
			} else {
				authMethod.setSite( SiteInstance.getAllInstance(authMethod.getForUcn(), authMethod.getForUcnSuffix()) );
			}
			Institution dbInstitution = DbInstitution.getByCode(authMethod.getForUcn());
			if (dbInstitution != null)
				authMethod.getSite().setInstitution( DbInstitution.getInstance( dbInstitution));
			else
				authMethod.getSite().setInstitution( InstitutionInstance.getEmptyInstance() );
		} else {
			authMethod.setSite( SiteInstance.getEmptyInstance());
		}
		
		if (authMethod.getProxyId() > 0) {
			Proxy dbProxy = DbProxy.getById(authMethod.getProxyId());
			if (dbProxy != null)
				authMethod.setProxy( DbProxy.getInstance(dbProxy) );
			else
				authMethod.setProxy( ProxyInstance.getUnknownInstance( authMethod.getProxyId() ) );
		} else {
			authMethod.setProxy(ProxyInstance.getEmptyInstance());
		}
	}
}
