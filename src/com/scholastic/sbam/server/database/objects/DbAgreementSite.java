package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.scholastic.sbam.server.database.codegen.AgreementSite;
import com.scholastic.sbam.server.database.codegen.AgreementSiteId;
import com.scholastic.sbam.server.database.codegen.CancelReason;
import com.scholastic.sbam.server.database.codegen.CommissionType;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.AgreementSiteInstance;
import com.scholastic.sbam.shared.objects.CancelReasonInstance;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbAgreementSite extends HibernateAccessor {
	
	static String objectName = AgreementSite.class.getSimpleName();
	
	public static AgreementSiteInstance getInstance(AgreementSite dbInstance) {
		AgreementSiteInstance instance = new AgreementSiteInstance();

		instance.setAgreementId(dbInstance.getId().getAgreementId());
		instance.setSiteUcn(dbInstance.getId().getSiteUcn());
		instance.setSiteUcnSuffix(dbInstance.getId().getSiteUcnSuffix());
		instance.setSiteLocCode(dbInstance.getId().getSiteLocCode());
		
		instance.setCommissionCode(dbInstance.getCommissionCode());
		
		instance.setCancelReasonCode(dbInstance.getCancelReasonCode());
		instance.setActiveDate(dbInstance.getActiveDate());
		instance.setInactiveDate(dbInstance.getInactiveDate());
		
		instance.setOrgPath(dbInstance.getOrgPath());
		instance.setNote(dbInstance.getNote());
		
		instance.setStatus(dbInstance.getStatus());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		
		return instance;
	}
	
	public static AgreementSite getById(int agreementId, int siteUcn, int siteUcnSuffix, String siteLocCode) {
		AgreementSiteId asid = new AgreementSiteId();
		asid.setAgreementId(agreementId);
		asid.setSiteUcn(siteUcn);
		asid.setSiteUcnSuffix(siteUcnSuffix);
		asid.setSiteLocCode(siteLocCode);
		try {
			AgreementSite instance = (AgreementSite) sessionFactory.getCurrentSession().get(getObjectReference(objectName), asid);
			return instance;
		} catch (RuntimeException re) {
        	re.printStackTrace();
            System.out.println(re.getMessage());
			throw re;
		}
	}
	
	public static List<AgreementSite> findAll() {
		List<Object> results = findAll(objectName);
		List<AgreementSite> reasons = new ArrayList<AgreementSite>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((AgreementSite) results.get(i));
		return reasons;
	}
	
	public static List<Order> getAscending(String... names) {
		List<Order> list = new ArrayList<Order>();
		for (String name : names)
			list.add(Order.asc(name));
		return list;
	}
	
	public static List<AgreementSite> findByAgreementId(int agreementId, char status, char neStatus) {
		return findByAgreementId(agreementId, status, neStatus, getAscending("id.siteUcn", "id.siteUcnSuffix", "id.siteLocCode"));
	}
	
	public static List<AgreementSite> findByAgreementId(int agreementId, char status, char neStatus, List<Order> orders) {
		return findByAgreementId(agreementId, -1, status, neStatus, orders);
	}
	
	public static List<AgreementSite> findByAgreementId(int agreementId, int ucn, char status, char neStatus) {
		return findByAgreementId(agreementId, ucn, status, neStatus, getAscending("id.siteUcn", "id.siteUcnSuffix", "id.siteLocCode"));
	}
	
	public static List<AgreementSite> findByAgreementId(int agreementId, int ucn, char status, char neStatus, List<Order> orders) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (agreementId > 0)
            	crit.add(Restrictions.eq("id.agreementId", agreementId));
            if (ucn > 0)
            	crit.add(Restrictions.eq("siteUcn", ucn));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            for (Order order : orders)
            	crit.addOrder(order);
            
//            crit.addOrder(Order.asc("id.siteUcn"));
//            crit.addOrder(Order.asc("id.siteUcnSuffix"));
//            crit.addOrder(Order.asc("id.siteLocCode"));
            
            @SuppressWarnings("unchecked")
			List<AgreementSite> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<AgreementSite>();
	}
	
	public static List<AgreementSite> findFiltered(int agreementId, String filter, boolean doBoolean, char neStatus) {
        try
        { 	
        	String sqlQuery = "SELECT agreement_site.* FROM agreement_site,institution WHERE ";
            sqlQuery += " agreement_site.agreement_id = " + agreementId;
            sqlQuery += " AND agreement_site.site_ucn = institution.ucn ";
           
            if (filter != null) {
				filter = filter.replaceAll("'", "''");
				if (doBoolean) {
					sqlQuery += "AND MATCH (institution_name,address1,adress2,adress3,city,zip,country,ucn,parent_ucn) AGAINST ('" + filter + "' IN BOOLEAN MODE)";
				} else {
					sqlQuery += "AND MATCH (institution_name,address1,adress2,adress3,city,zip,country) AGAINST ('" + filter + "')";
				}
            }
			
            if (neStatus != AppConstants.STATUS_ANY_NONE)
            	sqlQuery += " AND agreement_site.status <> '" + neStatus + "' ";
            sqlQuery += " order by institution.institution_name, institution.ucn";
            
            SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
            
            query.addEntity(getObjectReference(objectName));
            
            @SuppressWarnings("unchecked")
			List<AgreementSite> objects = query.list();
            
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<AgreementSite>();
	}
	
	public static List<Institution> findInstitutions(int agreementId, String filter, boolean doBoolean, char neStatus) {
        try
        { 	
        	String sqlQuery = "SELECT distinct institution.* FROM institution,agreement_site WHERE ";
            sqlQuery += " agreement_site.agreement_id = " + agreementId;
            sqlQuery += " AND agreement_site.site_ucn = institution.ucn ";
           

            if (filter != null) {
				filter = filter.replaceAll("'", "''");
				if (doBoolean) {
					sqlQuery += "AND MATCH (institution_name,address1,city,zip,ucn,parent_ucn) AGAINST ('" + filter + "' IN BOOLEAN MODE)";
				} else {
					sqlQuery += "AND MATCH (institution_name,address1,city,zip,ucn,parent_ucn) AGAINST ('" + filter + "')";
				}
            }
			
            if (neStatus != AppConstants.STATUS_ANY_NONE)
            	sqlQuery += " AND agreement_site.status <> '" + neStatus + "' ";
            sqlQuery += " order by institution.institution_name, institution.ucn";
            
            SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
            
            query.addEntity(DbInstitution.getObjectReference(DbInstitution.objectName));
            
            @SuppressWarnings("unchecked")
			List<Institution> objects = query.list();
            
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<Institution>();
	}
	

	
	public static List<Object []> findByUcn(List<Integer> ucns, char neStatus) {
        try
        {
        	if (ucns == null || ucns.size() == 0)
        		return new ArrayList<Object []>();
        		
        	StringBuffer sqlQuery = new StringBuffer();
        	sqlQuery.append("SELECT {agreement.*}, {agreement_site.*}, {institution.*} FROM agreement, agreement_site, institution WHERE agreement.`status` <> '");
        	sqlQuery.append(neStatus);
        	sqlQuery.append("'  AND agreement_site.status <> '");
        	sqlQuery.append(neStatus);
        	sqlQuery.append("' AND agreement.id = agreement_site.agreement_id "); 
        	sqlQuery.append(" AND agreement_site.site_ucn = institution.ucn ");
        	
        	sqlQuery.append(" AND ucn in (");
        	for (int i = 0; i < ucns.size(); i++) {
        		if (i > 0)
        			sqlQuery.append(",");
        		sqlQuery.append(ucns.get(i));
        	}
        	sqlQuery.append(")");
        	
            sqlQuery.append(" order by agreement_site.agreement_id, institution.institution_name, institution.ucn");
        	
//        	System.out.println(sqlQuery);
            
            SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery.toString());
            
            query.addEntity("agreement",		getObjectReference("Agreement"));
            query.addEntity("agreement_site",	getObjectReference("AgreementSite"));
            query.addEntity("institution",		getObjectReference("Institution"));
            
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
	
	public static List<Object []> findByNote(String filter, boolean doBoolean, char status, char neStatus, String sortField, SortDir sortDirection) {
        try
        {
        	if (filter == null || filter.trim().length() == 0)
        		return new ArrayList<Object []>();
        		
//        	AppConstants.TypedTerms typedTerms = AppConstants.parseTypedFilterTerms(filter);
        		
        	String sqlQuery = "SELECT {agreement.*}, {agreement_site.*}, {site.*}, {institution.*} FROM agreement, agreement_site, site, institution WHERE agreement.`status` <> '" + neStatus + "' " +
        						" AND agreement_site.status <> '" + neStatus + "' " +
        						" AND agreement.id = agreement_site.agreement_id " + 
        						" AND agreement_site.site_ucn = site.ucn " + 
        						" AND agreement_site.site_ucn_suffix = site.ucn_suffix " + 
        						" AND agreement_site.site_loc_code = site.site_loc_code " + 
        						" AND agreement_site.site_ucn = institution.ucn ";

        	if (status != AppConstants.STATUS_ANY_NONE)
        		sqlQuery += " AND agreement.status = '" + status + "'";
        	
        	filter = filter.replaceAll("'", "''");
			if (doBoolean) {
				sqlQuery += " AND MATCH (agreement_site.note) AGAINST ('" + filter + "' IN BOOLEAN MODE)";
			} else {
				sqlQuery += " AND MATCH (agreement_site.note) AGAINST ('" + filter + "')";
			}
        	
            sqlQuery += " order by ";
            if (sortField != null && sortField.length() > 0) {
            	sqlQuery += sortField;
            	if (sortDirection == SortDir.DESC)
            		sqlQuery += " DESC,";
            	else
            		sqlQuery += ",";
            }
            sqlQuery += " agreement_site.agreement_id, institution.institution_name, institution.ucn";
            
//			System.out.println(sqlQuery);
            
            SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
            
            query.addEntity("agreement",		getObjectReference("Agreement"));
            query.addEntity("agreement_site",	getObjectReference("AgreementSite"));
            query.addEntity("site",				getObjectReference("Site"));
            query.addEntity("institution",		getObjectReference("Institution"));
            
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
	
	public static void setDescriptions(AgreementSiteInstance agreementSite) {
		setDescriptions(agreementSite, null);
	}
	
	public static void setDescriptions(AgreementSiteInstance agreementSite, InstitutionInstance siteInstitution) {
		setDescriptions(agreementSite, null, siteInstitution);
	}	

	public static void setDescriptions(AgreementSiteInstance agreementSite, SiteInstance site, InstitutionInstance siteInstitution) {
		if (agreementSite == null)
			return;
		
		if (agreementSite.getSiteUcn() > 0) {
			if (site == null) {
				if (agreementSite.getSiteLocCode() != null && agreementSite.getSiteLocCode().length() > 0) {
					Site dbSite = DbSite.getById(agreementSite.getSiteUcn(), agreementSite.getSiteUcnSuffix(), agreementSite.getSiteLocCode());
					if (dbSite != null)
						agreementSite.setSite( DbSite.getInstance(dbSite) );
					else
						agreementSite.setSite( SiteInstance.getUnknownInstance(agreementSite.getSiteUcn(), agreementSite.getSiteUcnSuffix(), agreementSite.getSiteLocCode()));
				} else {
					agreementSite.setSite(SiteInstance.getAllInstance(agreementSite.getSiteUcn(), agreementSite.getSiteUcnSuffix()));
				}
				DbSite.setDescriptions(agreementSite.getSite(), siteInstitution);
			} else {
				if (agreementSite.getSite().getInstitution() == null)
					agreementSite.getSite().setInstitution(siteInstitution);
			}
		} else {
			agreementSite.setSite(SiteInstance.getEmptyInstance());
		}
		
		if (agreementSite.getCommissionCode() != null && agreementSite.getCommissionCode().length() > 0) {
			CommissionType commissionType = DbCommissionType.getByCode(agreementSite.getCommissionCode());
			if (commissionType != null) {
				agreementSite.setCommissionType(DbCommissionType.getInstance(commissionType));
			} else {
				agreementSite.setCommissionType(CommissionTypeInstance.getUnknownInstance(agreementSite.getCommissionCode()));
			}
		} else
			agreementSite.setCommissionType(CommissionTypeInstance.getEmptyInstance());
		
		
		if (agreementSite.getCancelReasonCode() != null && agreementSite.getCancelReasonCode().length() > 0) {
			CancelReason cancelReason = DbCancelReason.getByCode(agreementSite.getCancelReasonCode());
			if (cancelReason != null) {
				agreementSite.setCancelReason(DbCancelReason.getInstance(cancelReason));
			} else {
				agreementSite.setCancelReason(CancelReasonInstance.getUnknownInstance(agreementSite.getCancelReasonCode()));
			}
		} else
			agreementSite.setCancelReason(CancelReasonInstance.getEmptyInstance());
	}
}
