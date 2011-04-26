package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbInstitution extends HibernateAccessor {
	
	public static String objectName = Institution.class.getSimpleName();
	
	public static InstitutionInstance getInstance(Institution dbInstance) {
		if (dbInstance == null)
			return null;
		
		InstitutionInstance instance = new InstitutionInstance();
		instance.setUcn(dbInstance.getUcn());
		instance.setInstitutionName(dbInstance.getInstitutionName());
		instance.setAddress1(dbInstance.getAddress1());
		instance.setAddress2(dbInstance.getAddress2());
		instance.setAddress3(dbInstance.getAddress3());
		instance.setCity(dbInstance.getCity());
		instance.setState(dbInstance.getState());
		instance.setZip(dbInstance.getZip());
		instance.setCountry(dbInstance.getCountry());
		instance.setPhone(dbInstance.getPhone());
		instance.setFax(dbInstance.getFax());
		instance.setWebUrl(dbInstance.getWebUrl());
		instance.setTypeCode(dbInstance.getTypeCode());
		instance.setGroupCode(dbInstance.getGroupCode());
		instance.setPublicPrivateCode(dbInstance.getPublicPrivateCode());
		instance.setCreatedDate(dbInstance.getCreatedDate());
		instance.setClosedDate(dbInstance.getClosedDate());
		instance.setAlternateIds(dbInstance.getAlternateIds());
		instance.setStatus(dbInstance.getStatus());
		
		return instance;
	}
	
	public static Institution getByCode(String code) {
		return (Institution) getByField(objectName, "ucn", Integer.parseInt(code), "institutionName");
	}
	
	public static Institution getByCode(int code) {
		return (Institution) getByField(objectName, "ucn", code, "institutionName");
	}
	
	public static List<Institution> findAll() {
		List<Object> results = findAll(objectName);
		List<Institution> reasons = new ArrayList<Institution>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((Institution) results.get(i));
		return reasons;
	}
	
	public static List<Institution> findAllActive() {
		return findFiltered(null, null, null, null, null, AppConstants.STATUS_ACTIVE, (char) 0);
	}
	
	public static List<Institution> findFiltered(Integer ucn, String institutionName, String city, String state, String zip, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (ucn != null)
            	crit.add(Restrictions.eq("ucn", ucn));
            if (institutionName != null && institutionName.length() > 0)
            	crit.add(Restrictions.like("institutionName", institutionName));
            if (city != null && city.length() > 0)
            	crit.add(Restrictions.like("city", city));
            if (state != null && state.length() > 0)
            	crit.add(Restrictions.like("state", state));
            if (zip != null && zip.length() > 0)
            	crit.add(Restrictions.like("zip", zip));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
        //    crit.addOrder(Order.asc("institutionName"));
            @SuppressWarnings("unchecked")
			List<Institution> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<Institution>();
	}
	
	public static List<Institution> findFiltered(Integer [] ucns, char status, char neStatus) {
		return findFiltered(ucns, status, neStatus, null, null);
	}
	
	public static List<Institution> findFiltered(Integer [] ucns, char status, char neStatus, String sortCol, SortDir sortDirection) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (ucns != null && ucns.length > 0)
            	crit.add(Restrictions.in("ucn", ucns));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            if (sortCol != null && sortCol.length() > 0) {
            	if (sortDirection == SortDir.ASC)
            		crit.addOrder(Order.asc(sortCol));
            	else
            		crit.addOrder(Order.desc(sortCol));
            }
            @SuppressWarnings("unchecked")
			List<Institution> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<Institution>();
	}
	
	public static List<Institution> findFiltered(List<Integer> ucns, char status, char neStatus) {
		return findFiltered(ucns, status, neStatus, null, null);
	}
	
	public static List<Institution> findFiltered(List<Integer> ucns, char status, char neStatus, String sortCol, SortDir sortDirection) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (ucns != null && ucns.size() > 0)
            	crit.add(Restrictions.in("ucn", ucns));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            if (sortCol != null && sortCol.length() > 0) {
            	if (sortDirection == SortDir.ASC)
            		crit.addOrder(Order.asc(sortCol));
            	else
            		crit.addOrder(Order.desc(sortCol));
            }
            @SuppressWarnings("unchecked")
			List<Institution> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<Institution>();
	}
}
