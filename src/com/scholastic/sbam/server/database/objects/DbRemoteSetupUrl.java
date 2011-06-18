package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.RemoteSetupUrl;
import com.scholastic.sbam.server.database.codegen.RemoteSetupUrlId;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.RemoteSetupUrlInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbRemoteSetupUrl extends HibernateAccessor {
	
	static String objectName = RemoteSetupUrl.class.getSimpleName();
	
	public static RemoteSetupUrlInstance getInstance(RemoteSetupUrl dbInstance) {
		RemoteSetupUrlInstance instance = new RemoteSetupUrlInstance();

		instance.setAgreementId(dbInstance.getId().getAgreementId());
		instance.setUcn(dbInstance.getId().getUcn());
		instance.setUcnSuffix(dbInstance.getId().getUcnSuffix());
		instance.setSiteLocCode(dbInstance.getId().getSiteLocCode());
		instance.setUrlId(dbInstance.getId().getUrlId());
		
		instance.setUrl(dbInstance.getUrl());

		instance.setForUcn(dbInstance.getForUcn());
		instance.setForUcnSuffix(dbInstance.getForUcnSuffix());
		instance.setForSiteLocCode(dbInstance.getForSiteLocCode());
		
		instance.setApproved(dbInstance.getApproved());
		instance.setActivated(dbInstance.getActivated());
		
		instance.setNote(dbInstance.getNote());
		instance.setOrgPath(dbInstance.getOrgPath());
		
		instance.setStatus(dbInstance.getStatus());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		
		return instance;
	}
	
	public static RemoteSetupUrl getById(int agreementId, int ucn, int ucnSuffix, String siteLocCode, int urlId) {
		RemoteSetupUrlId amid = new RemoteSetupUrlId();
		amid.setAgreementId(agreementId);
		amid.setUcn(ucn);
		amid.setUcnSuffix(ucnSuffix);
		amid.setSiteLocCode(siteLocCode);
		amid.setUrlId(urlId);
		try {
			RemoteSetupUrl instance = (RemoteSetupUrl) sessionFactory.getCurrentSession().get(getObjectReference(objectName), amid);
			return instance;
		} catch (RuntimeException re) {
        	re.printStackTrace();
            System.out.println(re.getMessage());
			throw re;
		}
	}
	
	public static List<RemoteSetupUrl> findAll() {
		List<Object> results = findAll(objectName);
		List<RemoteSetupUrl> reasons = new ArrayList<RemoteSetupUrl>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((RemoteSetupUrl) results.get(i));
		return reasons;
	}
	
	public static List<RemoteSetupUrl> findByAgreementId(int agreementId, char status, char neStatus) {
		return findByOwner(agreementId, -1, -1, null, status, neStatus);
	}
	
	public static List<RemoteSetupUrl> findBySite( int ucn, int ucnSuffix, String siteLocCode, char status, char neStatus) {
		return findByOwner(-1, ucn, ucnSuffix, siteLocCode, status, neStatus);
	}
	
	public static List<RemoteSetupUrl> findByOwner(int agreementId, int ucn, int ucnSuffix, String siteLocCode, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (agreementId >= 0)
            	crit.add(Restrictions.eq("id.agreementId", agreementId));
            if (ucn >= 0)
            	crit.add(Restrictions.eq("id.ucn", ucn));
            if (ucnSuffix >= 0)
            	crit.add(Restrictions.eq("id.ucnSuffix", ucnSuffix));
            if (siteLocCode != null)
            	crit.add(Restrictions.eq("id.siteLocCode", siteLocCode));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("id.agreementId"));
            crit.addOrder(Order.asc("id.ucn"));
            crit.addOrder(Order.asc("id.ucnSuffix"));
            crit.addOrder(Order.asc("id.siteLocCode"));
            @SuppressWarnings("unchecked")
			List<RemoteSetupUrl> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("Agreement ID " + agreementId + ", " + ucn + ", " + ucnSuffix + ", Loc " + siteLocCode + ", status " + status + ", ne status " + neStatus);
        }
        return new ArrayList<RemoteSetupUrl>();
	}

	public static int getNextRemoteSetupUrlId(int agreementId, int ucn, int ucnSuffix, String siteLocCode) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
        crit.add(Restrictions.eq("id.agreementId", 	agreementId));
        crit.add(Restrictions.eq("id.ucn", 			ucn));
        crit.add(Restrictions.eq("id.ucnSuffix", 	ucnSuffix));
        crit.add(Restrictions.eq("id.siteLocCode", 	siteLocCode));
        crit.setMaxResults(1);
        crit.addOrder(Order.desc("urlId"));
        @SuppressWarnings("unchecked")
		List<RemoteSetupUrl> objects = crit.list();
        if (objects == null || objects.size() == 0)
        	return 1;
        return objects.get(0).getId().getUrlId() + 1;
	}
	
	public static List<RemoteSetupUrl> findByUrl(String url) {
		if (url == null || url.length() == 0)
			return new ArrayList<RemoteSetupUrl>();
		
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));

        //	This criterion is for database performance (i.e. prevents a full database scan)
        crit.add(Restrictions.eq("url", url));
        
        @SuppressWarnings("unchecked")
		List<RemoteSetupUrl> objects = crit.list();
        return objects;
	}
	
	public static List<Object []> findFiltered(String filter, boolean doBoolean, char neStatus) {
		if (filter == null || filter.length() == 0)
			return new ArrayList<Object []>();
		
    	AppConstants.TypedTerms typedTerms = AppConstants.parseTypedFilterTerms(filter, false);
    	
    	return findFiltered(typedTerms, doBoolean, neStatus);
	}
	
    public static List<Object []> findFiltered(AppConstants.TypedTerms typedTerms, boolean doBoolean, char neStatus) {
    	
    	String sqlQuery = "SELECT {agreement.*}, {remote_setup_url.*} FROM agreement, remote_setup_url WHERE agreement.`status` <> '" + neStatus + "' " +
		" AND remote_setup_url.status <> '" + neStatus + "' " +
		" AND remote_setup_url.agreement_id > 0 " +
		" AND agreement.id = remote_setup_url.agreement_id ";

    	sqlQuery += " AND ( /* 1 */ ";	//	-->1
    	
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
    			if (doBoolean && !word.startsWith("+"))
    				fullTextMatch.append("+");
    			fullTextMatch.append(word);
    			if (doBoolean && !word.endsWith("*"))
    				fullTextMatch.append("*");
    			fullTextMatch.append(" ");
    			sqlQuery += " ( url > ' ' AND ";
    			sqlQuery += " url like '%";
    			sqlQuery += word;
    			sqlQuery += "%') ";
    			termCount++;
    		}
    		
    		/* Anything with a number in the URL or the user ID starting with the number */
    		for (String number : typedTerms.getNumbers()) {
    			if (termCount > 0)
    				sqlQuery += " OR ";
    			if (doBoolean && !number.startsWith("+"))
    				fullTextMatch.append("+");
    			fullTextMatch.append(number);
    			if (doBoolean && !number.endsWith("*"))
    				fullTextMatch.append("*");
    			fullTextMatch.append(" ");
    			sqlQuery += " ( url > ' ' AND ";
    			sqlQuery += " url like '%";
    			sqlQuery += number;
    			sqlQuery += "%') ";
    			termCount++;
    		}
    		
    		if (fullTextMatch.length() > 0) {
        		if (termCount > 0)
        			sqlQuery += " OR ";
	    		/* Anything with all of the words and numbers in the note */
	    		if (doBoolean)
	    			sqlQuery += "MATCH (remote_setup_url.note) AGAINST ('" + fullTextMatch + "' IN BOOLEAN MODE ) ";
	    		else
	    			sqlQuery += "MATCH (remote_setup_url.note) AGAINST ('" + fullTextMatch + "') ";
    		}
    			 
    		sqlQuery += ") /* 2b */";	// <--2
    	}
    	
    	sqlQuery += ") /* 1 */ ";	// <--1
    	
//    	System.out.println(sqlQuery);
    	        	
		sqlQuery += " order by remote_setup_url.agreement_id, remote_setup_url.url";
		
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
		
		query.addEntity("agreement",		getObjectReference("Agreement"));
		query.addEntity("remote_setup_url",	getObjectReference("RemoteSetupUrl"));
		
		@SuppressWarnings("unchecked")
		List<Object []> objects = query.list();
		return objects;
	}
	
	public static List<Object []> findByNote(String filter, boolean doBoolean, char status, char neStatus, String sortField, SortDir sortDirection) {
        try
        {
        	if (filter == null || filter.trim().length() == 0)
        		return new ArrayList<Object []>();
        		
//        	AppConstants.TypedTerms typedTerms = AppConstants.parseTypedFilterTerms(filter);
        		
        	String sqlQuery = "SELECT {agreement.*}, {remote_setup_url.*} FROM agreement, remote_setup_url WHERE agreement.`status` <> '" + neStatus + "' " +
        						" AND remote_setup_url.status <> '" + neStatus + "' " +
        						" AND remote_setup_url.agreement_id > 0 " +
        						" AND agreement.id = remote_setup_url.agreement_id ";

        	if (status != AppConstants.STATUS_ANY_NONE)
        		sqlQuery += " AND agreement.status = '" + status + "'";
        	
        	filter = filter.replaceAll("'", "''");
			if (doBoolean) {
				sqlQuery += " AND MATCH (remote_setup_url.note) AGAINST ('" + filter + "' IN BOOLEAN MODE)";
			} else {
				sqlQuery += " AND MATCH (remote_setup_url.note) AGAINST ('" + filter + "')";
			}
        	
            sqlQuery += " order by ";
            if (sortField != null && sortField.length() > 0) {
            	sqlQuery += sortField;
            	if (sortDirection == SortDir.DESC)
            		sqlQuery += " DESC,";
            	else
            		sqlQuery += ",";
            }
            sqlQuery += " remote_setup_url.agreement_id, remote_setup_url.url";
            
//			System.out.println(sqlQuery);
            
            SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
            
            query.addEntity("agreement",			getObjectReference("Agreement"));
            query.addEntity("remote_setup_url",		getObjectReference("RemoteSetupUrl"));
            
            @SuppressWarnings("unchecked")
			List<Object []> objects = query.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<Object []>();
	}
	
	public static void setDescriptions(RemoteSetupUrlInstance remoteSetupUrl) {
		if (remoteSetupUrl == null)
			return;
		
		if (remoteSetupUrl.getUcn() > 0) {
			if (remoteSetupUrl.getSiteLocCode() != null && remoteSetupUrl.getSiteLocCode().length() > 0) {
				Site dbSite = DbSite.getById(remoteSetupUrl.getUcn(), remoteSetupUrl.getUcnSuffix(), remoteSetupUrl.getSiteLocCode());
				if (dbSite != null)
					remoteSetupUrl.setSite( DbSite.getInstance(dbSite) );
				else
					remoteSetupUrl.setSite( SiteInstance.getUnknownInstance( remoteSetupUrl.getUcn(), remoteSetupUrl.getUcnSuffix(), remoteSetupUrl.getSiteLocCode()) );
			} else {
				remoteSetupUrl.setSite( SiteInstance.getAllInstance(remoteSetupUrl.getUcn(), remoteSetupUrl.getUcnSuffix()) );
			}
			Institution dbInstitution = DbInstitution.getByCode(remoteSetupUrl.getUcn());
			if (dbInstitution != null)
				remoteSetupUrl.getSite().setInstitution( DbInstitution.getInstance( dbInstitution));
			else
				remoteSetupUrl.getSite().setInstitution( InstitutionInstance.getEmptyInstance() );
		} else {
			remoteSetupUrl.setSite( SiteInstance.getEmptyInstance());
		}
	}
	
	public static List<RemoteSetupUrl> findBySite( int ucn, int ucnSuffix, String siteLocCode, String methodType, char status, char neStatus) {
		return findByOwner(-1, ucn, ucnSuffix, siteLocCode, methodType, status, neStatus);
	}
	
	public static List<RemoteSetupUrl> findByOwner(int agreementId, int ucn, int ucnSuffix, String siteLocCode, String methodType, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (agreementId >= 0)
            	crit.add(Restrictions.eq("id.agreementId", agreementId));
            if (ucn >= 0)
            	crit.add(Restrictions.eq("id.ucn", ucn));
            if (ucnSuffix >= 0)
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
            crit.addOrder(Order.asc("url"));
            @SuppressWarnings("unchecked")
			List<RemoteSetupUrl> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("Agreement ID " + agreementId + ", " + ucn + ", " + ucnSuffix + ", Loc " + siteLocCode + ", Type " + methodType + ", status " + status + ", ne status " + neStatus);
        }
        return new ArrayList<RemoteSetupUrl>();
	}
	
	public static List<RemoteSetupUrl> findBySite(int agreementId, int ucn, int ucnSuffix, String siteLocCode, String methodType, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (agreementId > 0)
            	crit.add(Restrictions.eq("id.agreementId", agreementId));
            if (ucn >= 0)
            	crit.add(Restrictions.eq("forUcn", ucn));
            if (ucnSuffix >= 0)
            	crit.add(Restrictions.eq("forUcnSuffix", ucnSuffix));
            if (siteLocCode != null)
            	crit.add(Restrictions.eq("forSiteLocCode", siteLocCode));
            if (methodType != null)
            	crit.add(Restrictions.eq("id.methodType", methodType));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("id.agreementId"));
            crit.addOrder(Order.asc("forUcn"));
            crit.addOrder(Order.asc("forUcnSuffix"));
            crit.addOrder(Order.asc("forSiteLocCode"));
            crit.addOrder(Order.asc("url"));
            @SuppressWarnings("unchecked")
			List<RemoteSetupUrl> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<RemoteSetupUrl>();
	}
}
