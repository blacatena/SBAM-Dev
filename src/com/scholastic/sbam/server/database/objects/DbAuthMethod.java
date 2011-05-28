package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
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
import com.scholastic.sbam.shared.objects.IpAddressInstance;
import com.scholastic.sbam.shared.objects.ProxyInstance;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.util.AppConstants;

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
	
	public static List<AuthMethod> findByUid(String uid) {
		if (uid == null || uid.length() == 0)
			return new ArrayList<AuthMethod>();
		
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));

        //	This criterion is for database performance (i.e. prevents a full database scan)
        crit.add(Restrictions.eq("userId", uid));
        
        @SuppressWarnings("unchecked")
		List<AuthMethod> objects = crit.list();
        return objects;
	}
	
	public static List<AuthMethod> findByUrl(String url) {
		if (url == null || url.length() == 0)
			return new ArrayList<AuthMethod>();
		
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));

        //	This criterion is for database performance (i.e. prevents a full database scan)
        crit.add(Restrictions.eq("url", url));
        
        @SuppressWarnings("unchecked")
		List<AuthMethod> objects = crit.list();
        return objects;
	}
	
	public static List<Object []> findFiltered(String filter, char neStatus) {
		if (filter == null || filter.length() == 0)
			return new ArrayList<Object []>();
		
    	AppConstants.TypedTerms typedTerms = AppConstants.parseTypedFilterTerms(filter, true);
    	
    	return findFiltered(typedTerms, neStatus);
	}
	
    public static List<Object []> findFiltered(AppConstants.TypedTerms typedTerms, char neStatus) {
    	
    	String sqlQuery = "SELECT {agreement.*}, {auth_method.*} FROM agreement, auth_method WHERE agreement.`status` <> '" + neStatus + "' " +
		" AND auth_method.status <> '" + neStatus + "' " +
		" AND auth_method.agreement_id > 0 " +
		" AND agreement.id = auth_method.agreement_id ";

    	sqlQuery += " AND ( /* 1 */ ";	//	-->1
    	if (typedTerms.getIps().size() > 0) {
    		sqlQuery += " ( /* 2a */ ";	// -->2
    		
    		/* Anything that qualifies by IP any range */
    		int ipCount = 0;
    		for (Long [] ipRange : typedTerms.getIps()) {
    			String ipRangeCode = IpAddressInstance.getCommonIpRangeCode(ipRange [0], ipRange [1]);
    			if (ipCount > 0)
    				sqlQuery += " OR ";
    			
    			sqlQuery += " ( /* 3a */ "; // -->3
    			sqlQuery += "(auth_method.ip_range_code in ("; // -->4
    			
    			if (ipRangeCode.length() > 1) {
	    			for (int i = 1; i < ipRangeCode.length(); i++) {
	    				if (i > 1)
	    					sqlQuery += ",";
	    				sqlQuery += "'" + ipRangeCode.substring(0,i) + "'";
	    			}
	    			
	    			sqlQuery += ") OR ";
    			}
    			sqlQuery += " auth_method.ip_range_code like '";
    			sqlQuery += ipRangeCode;
    			sqlQuery += "%' ) AND ip_lo <= "; // <--4
    			sqlQuery += ipRange [1];
    			sqlQuery += " AND ip_hi >= ";
    			sqlQuery += ipRange [0];
    			sqlQuery += ") /* 3a */ ";	// <--3
    			
    			ipCount++;
    		}
//    		sqlQuery += " ) /* 2a */ "; // <--2
    		
//    		if (typedTerms.getNumbers().size() > 0) {
//    			sqlQuery += " AND ( /* 3b */ ";	// -->3
//    			
//    			int termCount = 0;
//    			for (String number : typedTerms.getNumbers()) {
//        			if (termCount > 0)
//        				sqlQuery += " OR ";
//        			sqlQuery += "agreement.id like '";
//        			sqlQuery += number;
//        			sqlQuery += "%'";
//        			termCount++;
//    			}
//    			
//    			sqlQuery += ") /* 3b */ ";	//	<--3
//    		}
    		
    		sqlQuery += " ) /* 2a */ ";	// <--2
    	}
    	
    	if (typedTerms.getWords().size() > 0 || typedTerms.getNumbers().size() > 0) {
    		
    		StringBuffer fullTextMatch = new StringBuffer();
    		int termCount = 0;
    		if (typedTerms.getIps().size() > 0)
    			sqlQuery += " OR ";
    		
    		/* Anything with either the word in the url, or the user ID starting with the word */
    		sqlQuery += " ( /* 2b */ ";	// -->2
    		for (String word : typedTerms.getWords()) {
    			if (termCount > 0)
    				sqlQuery += " OR ";
    			word = word.replace("'", "''");
    			fullTextMatch.append("+");
    			fullTextMatch.append(word);
    			fullTextMatch.append(" ");
    			sqlQuery += " ( url > ' ' AND ";
    			sqlQuery += " url like '%";
    			sqlQuery += word;
    			sqlQuery += "%') OR (user_id like '";
    			sqlQuery += word;
    			sqlQuery += "%') ";
    			termCount++;
    		}
    		
    		/* Anything with a number in the URL or the user ID starting with the number */
    		for (String number : typedTerms.getNumbers()) {
    			if (termCount > 0)
    				sqlQuery += " OR ";
    			fullTextMatch.append("+");
    			fullTextMatch.append(number);
    			fullTextMatch.append("* ");
    			sqlQuery += " ( url > ' ' AND ";
    			sqlQuery += " url like '%";
    			sqlQuery += number;
    			sqlQuery += "%') OR (user_id like '";
    			sqlQuery += number;
    			sqlQuery += "%') ";
    			termCount++;
    		}
    		
    		if (termCount > 0)
    			sqlQuery += " OR ";
    		/* Anything with all of the words and numbers in the note */
    		sqlQuery += "MATCH (auth_method.note) AGAINST ('" + fullTextMatch + "') ";
    		
    		sqlQuery += ") /* 2b */";	// <--2
    	}
    	
    	sqlQuery += ") /* 1 */ ";	// <--1
    	
//    	System.out.println(sqlQuery);
    	        	
		sqlQuery += " order by auth_method.agreement_id, auth_method.ip_lo, auth_method.ip_hi, auth_method.user_id, auth_method.password, auth_method.url";
		
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
		
		query.addEntity("agreement",	getObjectReference("Agreement"));
		query.addEntity("auth_method",	getObjectReference("AuthMethod"));
		
		@SuppressWarnings("unchecked")
		List<Object []> objects = query.list();
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
