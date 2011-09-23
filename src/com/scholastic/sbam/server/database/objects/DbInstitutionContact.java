package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.scholastic.sbam.server.database.codegen.Contact;
import com.scholastic.sbam.server.database.codegen.InstitutionContact;
import com.scholastic.sbam.server.database.codegen.InstitutionContactId;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.ContactInstance;
import com.scholastic.sbam.shared.objects.InstitutionContactInstance;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbInstitutionContact extends HibernateAccessor {
	
	static String objectName = InstitutionContact.class.getSimpleName();
	
	public static InstitutionContactInstance getInstance(InstitutionContact dbInstance) {
		InstitutionContactInstance instance = new InstitutionContactInstance();

		instance.setUcn(dbInstance.getId().getUcn());
		instance.setContactId(dbInstance.getId().getContactId());
		
		instance.setStatus(dbInstance.getStatus());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		
		return instance;
	}
	
	public static InstitutionContact getById(int ucn, int contactId) {
		InstitutionContactId scid = new InstitutionContactId();
		scid.setUcn(ucn);
		scid.setContactId(contactId);
		try {
			InstitutionContact instance = (InstitutionContact) sessionFactory.getCurrentSession().get(getObjectReference(objectName), scid);
			return instance;
		} catch (RuntimeException re) {
        	re.printStackTrace();
            System.out.println(re.getMessage());
			throw re;
		}
	}
	
	public static List<InstitutionContact> findAll() {
		List<Object> results = findAll(objectName);
		List<InstitutionContact> reasons = new ArrayList<InstitutionContact>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((InstitutionContact) results.get(i));
		return reasons;
	}
	
	public static List<InstitutionContact> findByUcn(int ucn, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (ucn > 0)
            	crit.add(Restrictions.eq("id.ucn", ucn));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("id.contactId"));
            @SuppressWarnings("unchecked")
			List<InstitutionContact> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<InstitutionContact>();
	}
	
	public static List<InstitutionContact> findByContactId(int contactId, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (contactId > 0)
            	crit.add(Restrictions.eq("id.contactId", contactId));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("id.ucn"));
            @SuppressWarnings("unchecked")
			List<InstitutionContact> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<InstitutionContact>();
	}
	
	public static List<Object []> findFiltered(String filter, boolean doBoolean, char status, char neStatus) {
		return findFiltered(filter, doBoolean, status, neStatus, null, null);
	}
	
	public static List<Object []> findFiltered(String filter, boolean doBoolean, char status, char neStatus, String sortField, SortDir sortDirection) {
        try
        {
        	if (filter == null || filter.trim().length() == 0)
        		return new ArrayList<Object []>();
        		
//        	AppConstants.TypedTerms typedTerms = AppConstants.parseTypedFilterTerms(filter);
        		
        	String sqlQuery = "SELECT {institution.*}, {institution_contact.*}, {contact.*} FROM institution, institution_contact, contact WHERE institution.`status` <> '" + neStatus + "' " +
        						" AND institution_contact.status <> '" + neStatus + "' " +
        						" AND institution.ucn = institution_contact.ucn " + 
        						" AND institution_contact.contact_id = contact.contact_id ";

        	if (status != AppConstants.STATUS_ANY_NONE)
        		sqlQuery += " AND institution.status = '" + status + "'";
        	
        	filter = filter.replaceAll("'", "''");
			if (doBoolean) {
				sqlQuery += " AND ( MATCH (contact.full_name,contact.address1,contact.city,contact.zip) AGAINST ('" + filter + "' IN BOOLEAN MODE)";
				sqlQuery += " OR MATCH (contact.e_mail,contact.e_mail_2,contact.phone,contact.phone_2,contact.fax) AGAINST ('" + filter + "' IN BOOLEAN MODE)";
				sqlQuery += " OR MATCH (contact.note) AGAINST ('" + filter + "' IN BOOLEAN MODE) ) ";
			} else {
				sqlQuery += " AND ( MATCH (contact.full_name,contact.address1,contact.city,contact.zip) AGAINST ('" + filter + "')";
				sqlQuery += " OR MATCH (contact.e_mail,contact.e_mail_2,contact.phone,contact.phone_2,contact.fax) AGAINST ('" + filter + "')";
				sqlQuery += " OR MATCH (contact.note) AGAINST ('" + filter + "') ) ";
			}
        	
        	
//        	if (typedTerms.getNumbers().size() > 0) {
//	        	sqlQuery += " AND ( ";
//	        	for (int i = 0; i < typedTerms.getNumbers().size(); i++) {
//	        		if (i > 0) sqlQuery += " OR ";
//	        		String numberLike = " like '%" + typedTerms.getNumbers().get(i) + "%' ";
//	        		sqlQuery += " institution.id" + numberLike;
//	        		sqlQuery += " OR institution.id_check_digit" + numberLike;
//	        	}
//	        	sqlQuery += " ) ";
//        	}
        	
//			We take advantage of the MATCH and full text index for this part
//        	if (typedTerms.getWords().size() > 0) {
//        		sqlQuery += " AND ( ";
//        		for (int i = 0; i < typedTerms.getWords().size(); i++) {
//        			if (i > 0) sqlQuery += " OR ";
//        			String wordLike = " like '%" + typedTerms.getWords().get(i) + "%' ";
//        			sqlQuery += " institution_contact.note" + wordLike;
//        			sqlQuery += " OR contact.description" + wordLike;
//        			//	Only apply it to contact code if it's a word, not a phrase (i.e. no blanks)
//        			if (typedTerms.getWords().get(i).indexOf(' ') < 0)
//        				sqlQuery += " OR contact.contact_code" + wordLike;
//        		}
//        		sqlQuery += " ) ";
//        	}
        	
            sqlQuery += " order by ";
            if (sortField != null && sortField.length() > 0) {
            	sqlQuery += sortField;
            	if (sortDirection == SortDir.DESC)
            		sqlQuery += " DESC,";
            	else
            		sqlQuery += ",";
            }
            sqlQuery += " contact.full_name, institution.institution_name";
            
 //			System.out.println(sqlQuery);
            
            SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
            
            query.addEntity("institution",		  getObjectReference("Institution"));
            query.addEntity("institution_contact",getObjectReference("InstitutionContact"));
            query.addEntity("contact",			  getObjectReference("Contact"));
            
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

	public static void setDescriptions(InstitutionContactInstance instance) {
		if (instance.getContact() == null) {
			if (instance.getContactId() > 0) {
				Contact contact = DbContact.getByCode(instance.getContactId());
				if (contact != null) {
					instance.setContact(DbContact.getInstance(contact));
					DbContact.setDescriptions(instance.getContact());
				} else
					instance.setContact(ContactInstance.getUnknownInstance(instance.getContactId()));
			} else {
				instance.setContact(ContactInstance.getEmptyInstance());
			}
		}
	}
}
