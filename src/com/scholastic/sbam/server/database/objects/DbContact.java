package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.scholastic.sbam.server.database.codegen.Contact;
import com.scholastic.sbam.server.database.codegen.ContactType;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.ContactInstance;
import com.scholastic.sbam.shared.objects.ContactTypeInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbContact extends HibernateAccessor {
	
	static String objectName = Contact.class.getSimpleName();
	
	public static ContactInstance getInstance(Contact dbInstance) {
		if (dbInstance == null)
			return null;
		
		ContactInstance instance = new ContactInstance();
		instance.setContactId(dbInstance.getContactId());
		instance.setParentUcn(dbInstance.getParentUcn());
		instance.setFullName(dbInstance.getFullName());
		instance.setContactTypeCode(dbInstance.getContactTypeCode());
		instance.setTitle(dbInstance.getTitle());
		instance.setAdditionalInfo(dbInstance.getAdditionalInfo());
		instance.setAddress1(dbInstance.getAddress1());
		instance.setAddress2(dbInstance.getAddress2());
		instance.setAddress3(dbInstance.getAddress3());
		instance.setCity(dbInstance.getCity());
		instance.setState(dbInstance.getState());
		instance.setZip(dbInstance.getZip());
		instance.setCountry(dbInstance.getCountry());
		instance.setPhone(dbInstance.getPhone());
		instance.setPhone2(dbInstance.getPhone2());
		instance.setFax(dbInstance.getFax());
		instance.seteMail(dbInstance.getEMail());
		instance.seteMail2(dbInstance.getEMail2());
		instance.setNote(dbInstance.getNote());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		instance.setStatus(dbInstance.getStatus());
		
		return instance;
	}
	
	public static Contact getByCode(String code) {
		return (Contact) getByField(objectName, "contactId", Integer.parseInt(code), "fullName");
	}
	
	public static Contact getByCode(int code) {
		return (Contact) getByField(objectName, "contactId", code, "fullName");
	}
	
	public static List<Contact> findAll() {
		List<Object> results = findAll(objectName);
		List<Contact> reasons = new ArrayList<Contact>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((Contact) results.get(i));
		return reasons;
	}
	
	public static List<Contact> findAllActive() {
		return findFiltered(0, null, null, null, AppConstants.STATUS_ACTIVE, (char) 0);
	}
	
	public static List<Contact> findFiltered(int contactId, String fullName, String email, String phone, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (contactId > 0)
            	crit.add(Restrictions.eq("contactId", contactId));
            if (fullName != null && fullName.length() > 0)
            	crit.add(Restrictions.like("fullName", fullName));
            if (email != null && email.length() > 0)
            	crit.add(Restrictions.like("eMail", email));
            if (phone != null && phone.length() > 0)
            	crit.add(Restrictions.like("phone", phone));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("fullName"));
            @SuppressWarnings("unchecked")
			List<Contact> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<Contact>();
	}
	
	public static List<Contact> findFiltered(Integer [] ucns, char status, char neStatus) {
		return findFiltered(ucns, status, neStatus, null, null);
	}
	
	public static List<Contact> findFiltered(Integer [] contactIds, char status, char neStatus, String sortCol, SortDir sortDirection) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (contactIds != null && contactIds.length > 0)
            	crit.add(Restrictions.in("contactId", contactIds));
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
			List<Contact> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<Contact>();
	}
	
	public static List<Contact> findFiltered(List<Integer> ucns, char status, char neStatus) {
		return findFiltered(ucns, status, neStatus, null, null);
	}
	
	public static List<Contact> findFiltered(List<Integer> contactIds, char status, char neStatus, String sortCol, SortDir sortDirection) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (contactIds != null && contactIds.size() > 0)
            	crit.add(Restrictions.in("contactId", contactIds));
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
			List<Contact> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<Contact>();
	}
	
	public static List<Contact> findByUcn(int ucn, char status, char neStatus) {
        try
        { 	
        	String sqlQuery = "SELECT contact.* FROM contact, site_contact WHERE ";
            sqlQuery += " site_contact.ucn = " + ucn;
            sqlQuery += " AND site_contact.contact_id = contact.contact_id ";
            if (status != AppConstants.STATUS_ANY_NONE)
            	sqlQuery += " AND contact.status = '" + status + "' ";
            if (neStatus != AppConstants.STATUS_ANY_NONE)
            	sqlQuery += " AND contact.status <> '" + neStatus + "' ";
            sqlQuery += " order by contact.full_name, contact.contact_id";
            
            SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
            
            query.addEntity(getObjectReference(objectName));
            
            @SuppressWarnings("unchecked")
			List<Contact> objects = query.list();
            
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<Contact>();
	}
	
	public static List<Contact> findFiltered(String filter, boolean doBoolean, char status, char neStatus, String sortCol, SortDir sortDirection) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            
			filter = filter.replaceAll("'", "''");
			String sql;
			if (doBoolean) {
				sql = "MATCH (full_name,address1,city,zip) AGAINST ('" + filter + "' IN BOOLEAN MODE)";
			} else {
				sql = "MATCH (full_name,address1,city,zip) AGAINST ('" + filter + "')";
			}
			crit.add(Restrictions.sqlRestriction(sql));
            
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            if (sortCol != null && sortCol.length() > 0) {
            	if (sortDirection == SortDir.ASC)
            		crit.addOrder(Order.asc(sortCol));
            	else
            		crit.addOrder(Order.desc(sortCol));
            } else {
            	crit.addOrder(Order.asc("fullName"));
            }
            @SuppressWarnings("unchecked")
			List<Contact> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<Contact>();
	}
	
	public static void setDescriptions(ContactInstance instance) {
		if (instance == null)
			return;
		
		if (instance.getContactTypeCode() != null && instance.getContactTypeCode().length() > 0) {
			ContactType contactType = DbContactType.getByCode(instance.getContactTypeCode());
			if (contactType == null)
				instance.setContactType(ContactTypeInstance.getUnknownInstance(instance.getContactTypeCode()));
			else
				instance.setContactType(DbContactType.getInstance(contactType));
		} else
			instance.setContactType(ContactTypeInstance.getEmptyInstance());
		
		
		if (instance.getParentUcn() > 0) {
			Institution dbInstitution = DbInstitution.getByCode(instance.getParentUcn());
			if (dbInstitution != null)
				instance.setInstitution( DbInstitution.getInstance(dbInstitution) );
			else
				instance.setInstitution( InstitutionInstance.getUnknownInstance( instance.getParentUcn()) );
		} else {
			instance.setInstitution( InstitutionInstance.getEmptyInstance());
		}
	}
}
