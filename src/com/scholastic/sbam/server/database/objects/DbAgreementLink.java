package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.AgreementLink;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.LinkType;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.AgreementLinkInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.LinkTypeInstance;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbAgreementLink extends HibernateAccessor {
	
	static String objectName = AgreementLink.class.getSimpleName();
	
	public static AgreementLinkInstance getInstance(AgreementLink dbInstance) {
		AgreementLinkInstance instance = new AgreementLinkInstance();

		instance.setLinkId(dbInstance.getLinkId());
		

		instance.setLinkIdCheckDigit(dbInstance.getLinkIdCheckDigit());
		instance.setUcn(dbInstance.getUcn());
		instance.setLinkTypeCode(dbInstance.getLinkTypeCode());
		instance.setNote(dbInstance.getNote());
		
		instance.setStatus(dbInstance.getStatus());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		
		return instance;
	}
	
	public static AgreementLink getById(int linkId) {
		try {
			AgreementLink instance = (AgreementLink) sessionFactory.getCurrentSession().get(getObjectReference(objectName), linkId);
//			if (instance == null) {
//				log.debug("get successful, no instance found");
//			} else {
//				log.debug("get successful, instance found");
//			}
			return instance;
		} catch (RuntimeException re) {
        	re.printStackTrace();
            System.out.println(re.getMessage());
			throw re;
		}
	}
	
	public static List<AgreementLink> findAll() {
		List<Object> results = findAll(objectName);
		List<AgreementLink> linkTypes = new ArrayList<AgreementLink>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				linkTypes.add((AgreementLink) results.get(i));
		return linkTypes;
	}
	
	public static List<Object []> findFiltered(String terms, char status, char neStatus) {
		String [] termsArray = AppConstants.parseFilterTerms(terms);
		return findFiltered(termsArray, status, neStatus);
	}
	
	@SuppressWarnings("unchecked")
	public static List<Object []> findFiltered(String [] terms, char status, char neStatus) {
        try
        {
        	String sqlQuery = "SELECT agreement_link.*, institution.* FROM agreement_link, institution WHERE ";
        	sqlQuery += " agreement_link.ucn = institution.ucn ";
        	
        	if (terms.length > 0) {
        		sqlQuery += " AND ( ";
	        	sqlQuery += getWhereLikeTerms(terms);
	            sqlQuery += " ) ";
        	}
        	
            if (status != AppConstants.STATUS_ANY_NONE)
                sqlQuery += " AND agreement_link.status = '" + status + "' ";
            else if (neStatus != AppConstants.STATUS_ANY_NONE)
	            sqlQuery += " AND agreement_link.status <> '" + neStatus + "' ";
            sqlQuery += " order by institution.institution_name, agreement_link.link_id";

            
            SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
                       
            query.addEntity("agreement_link", AgreementLink.class);
            query.addEntity("institution", Institution.class);
            
			return query.list();
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<Object []>();
	}
	
	public static String getWhereLikeTerms(String [] terms) {
//		StringBuffer sb = new StringBuffer();
		
//		StringBuffer intTerms = new StringBuffer();
		StringBuffer strTerms = new StringBuffer();
		
		for (String term : terms) {
//			//	Numeric values may be agreement link IDs or UCNs
//			if (AppConstants.isNumeric(term)) {
//				if (intTerms.length() > 0)
//					intTerms.append(" OR ");
//				intTerms.append("agreement_link.link_id = ");
//				intTerms.append(term);
//				intTerms.append(" OR agreement_link.ucn = ");
//				intTerms.append(term);
//				intTerms.append(" ");
//			}
			
			if (strTerms.length() > 0)
				strTerms.append(" AND ");
			
			strTerms.append("(");
			
			if (AppConstants.isNumeric(term)) {
				appendLike(strTerms, "agreement_link.link_id_check_digit", term);
				strTerms.append(" OR ");
				appendLike(strTerms, "institution.ucn", term);
				strTerms.append(" OR ");
			}
			appendLike(strTerms, "institution.institution_name", term);
			strTerms.append(" OR ");
			appendLike(strTerms, "institution.address1", term);
			strTerms.append(" OR ");
			appendLike(strTerms, "institution.address2", term);
			strTerms.append(" OR ");
			appendLike(strTerms, "institution.address3", term);
			strTerms.append(" OR ");
			appendLike(strTerms, "institution.city", term);
			strTerms.append(" OR ");
			appendLike(strTerms, "institution.state", term);
			strTerms.append(" OR ");
			appendLike(strTerms, "institution.zip", term);
			strTerms.append(" OR ");
			appendLike(strTerms, "institution.country", term);
			strTerms.append(" OR ");
			appendLike(strTerms, "institution.phone", term);
			strTerms.append(" OR ");
			appendLike(strTerms, "institution.fax", term);
			strTerms.append(" OR ");
			appendLike(strTerms, "institution.alternate_ids", term);
			
			strTerms.append(")");
		}
		
		return strTerms.toString();
	}
	
	public static void appendLike(StringBuffer sb, String field, String term) {
		sb.append(field);
		sb.append(" like '%");
		sb.append(term.trim());
		sb.append("%' ");
	}
	
	public static List<AgreementLink> findByUcn(int ucn, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (ucn > 0)
            	crit.add(Restrictions.eq("ucn", ucn));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("createdDatetime"));
            @SuppressWarnings("unchecked")
			List<AgreementLink> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<AgreementLink>();
	}
	
	/**
	 * Set the accompanying instances for an Agreement Link instance.  Note that if the institution is already set, it will not be re-set (i.e. no unnecessary DB access).
	 * @param agreementLink
	 */
	public static void setDescriptions(AgreementLinkInstance agreementLink) {
		if (agreementLink == null)
			return;
		
		if (agreementLink.getLinkTypeCode() != null) {
			LinkType tType = DbLinkType.getByCode(agreementLink.getLinkTypeCode());
			if (tType != null) {
				agreementLink.setLinkType(DbLinkType.getInstance(tType));
			} else {
				agreementLink.setLinkType(LinkTypeInstance.getUnknownInstance(agreementLink.getLinkTypeCode()));
			}
		} else {
			agreementLink.setLinkType(LinkTypeInstance.getUnknownInstance("none"));
		}
	
		
		if (agreementLink.getInstitution() == null) {
			if (agreementLink.getUcn() >= 0) {
				Institution institution = DbInstitution.getByCode(agreementLink.getUcn());
				if (institution != null) {
					agreementLink.setInstitution(DbInstitution.getInstance(institution));
				} else {
					agreementLink.setInstitution(InstitutionInstance.getUnknownInstance(agreementLink.getUcn()));
				}
			} else {
				agreementLink.setInstitution(InstitutionInstance.getEmptyInstance());
			}
		}
		
	}
}
