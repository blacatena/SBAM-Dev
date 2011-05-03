package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.AgreementLink;
import com.scholastic.sbam.server.database.codegen.AgreementTerm;
import com.scholastic.sbam.server.database.codegen.AgreementType;
import com.scholastic.sbam.server.database.codegen.CommissionType;
import com.scholastic.sbam.server.database.codegen.DeleteReason;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.objects.AgreementLinkInstance;
import com.scholastic.sbam.shared.objects.AgreementSummaryInstance;
import com.scholastic.sbam.shared.objects.AgreementTypeInstance;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.objects.DeleteReasonInstance;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbAgreement extends HibernateAccessor {
	
	static String objectName = Agreement.class.getSimpleName();
	
	public static AgreementInstance getInstance(Agreement dbInstance) {
		AgreementInstance instance = new AgreementInstance();
		
		instance.setId(dbInstance.getId());
		instance.setBillUcn(dbInstance.getBillUcn());
		instance.setBillUcnSuffix(dbInstance.getBillUcnSuffix());
		instance.setAgreementTypeCode(dbInstance.getAgreementTypeCode());
		instance.setCommissionCode(dbInstance.getCommissionCode());
		instance.setDeleteReasonCode(instance.getDeleteReasonCode());
		instance.setOrgPath(dbInstance.getOrgPath());
		instance.setNote(dbInstance.getNote());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		instance.setStatus(dbInstance.getStatus());
		
		return instance;
	}
	
	public static Agreement getById(String id) {
		return (Agreement) DbAgreement.getById(objectName, id);
	}
	
	public static Agreement getById(int id) {
		return (Agreement) DbAgreement.getById(objectName, id);
	}
	
	public static List<Agreement> findAll() {
		List<Object> results = findAll(objectName);
		List<Agreement> reasons = new ArrayList<Agreement>();
		for (int i = 0; i < results.size(); i++)
			reasons.add((Agreement) results.get(i));
		return reasons;
	}
	
	public static List<Agreement> findByUcn(int ucn) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(Agreement.class);
            if (ucn > 0)
            	crit.add(Restrictions.like("ucn", ucn));
            crit.addOrder(Order.asc("ucn"));
            crit.addOrder(Order.asc("agreementId"));
            @SuppressWarnings("unchecked")
			List<Agreement> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<Agreement>();
	}
	
	// Combined Agreement Summaries
	
	public static SortedMap<Integer, AgreementSummaryInstance> findAllAgreementSummaries(int ucn,boolean primary, char status, char neStatus) throws Exception {
		return findAllAgreementSummaries(ucn, null, primary, status, neStatus);
	}
	
	public static SortedMap<Integer, AgreementSummaryInstance> findAllAgreementSummaries(List<Integer> ucns,boolean primary, char status, char neStatus) throws Exception {
		return findAllAgreementSummaries(0, ucns, primary, status, neStatus);
	}
	
	public static SortedMap<Integer, AgreementSummaryInstance> findAllAgreementSummaries(int ucn, List<Integer> ucns, boolean primary, char status, char neStatus) throws Exception {
		SortedMap<Integer, AgreementSummaryInstance> agreementMap = findBillAgreementSummaries(ucn, null, primary, status, neStatus);
		agreementMap.putAll(findSiteAgreementSummaries(ucn, primary,status,neStatus));
		return agreementMap;
	}
	
	// Site Agreement Summaries
	
	public static SortedMap<Integer, AgreementSummaryInstance> findSiteAgreementSummaries(int ucn,boolean primary, char status, char neStatus) throws Exception {
		return findSiteAgreementSummaries(ucn, null, primary, status, neStatus);
	}
	
	public static SortedMap<Integer, AgreementSummaryInstance> findSiteAgreementSummaries(List<Integer> ucns,boolean primary, char status, char neStatus) throws Exception {
		return findSiteAgreementSummaries(0, ucns, primary, status, neStatus);
	}
	
	public static SortedMap<Integer, AgreementSummaryInstance> findSiteAgreementSummaries(int ucn, List<Integer> ucns, boolean primary, char status, char neStatus) throws Exception {
		/*
		 * IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT 
		 * 
		 * Because of the way Hibernate works, it is critical that the SQL below be constructed with {} around the column names, 
		 * and then the appropriate entity references be explicitly add where this query is used.  Without this, Hibernate gets confused about the
		 * column names so when the same name appears in both tables (such as STATUS and CREATED_DATETIME) then both table entities
		 * will get the values of the first occurrence of the column name (i.e. the value from the first table).
		 */
		String sqlQuery = getSiteSummarySql(ucn, null, primary, status, neStatus);
            
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);            
        query.addEntity("agreement", Agreement.class);
        query.addEntity("agreement_term", AgreementTerm.class);
        
        @SuppressWarnings("unchecked")
		List<Object []> objects = query.list();

		return getAgreementSummaryList(objects, primary, true);
	}
	
	// Bill Agreement Summaries
	
	public static SortedMap<Integer, AgreementSummaryInstance> findBillAgreementSummaries(int ucn,boolean primary, char status, char neStatus) throws Exception {
		return findBillAgreementSummaries(ucn, null, primary, status, neStatus);
	}
	
	public static SortedMap<Integer, AgreementSummaryInstance> findBillAgreementSummaries(List<Integer> ucns,boolean primary, char status, char neStatus) throws Exception {
		return findBillAgreementSummaries(0, ucns, primary, status, neStatus);
	}
	
	public static SortedMap<Integer, AgreementSummaryInstance> findBillAgreementSummaries(int ucn, List<Integer> ucns, boolean primary, char status, char neStatus) throws Exception {
		/*
		 * IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT 
		 * 
		 * Because of the way Hibernate works, it is critical that the SQL below be constructed with {} around the column names, 
		 * and then the appropriate entity references be explicitly add where this query is used.  Without this, Hibernate gets confused about the
		 * column names so when the same name appears in both tables (such as STATUS and CREATED_DATETIME) then both table entities
		 * will get the values of the first occurrence of the column name (i.e. the value from the first table).
		 */
    	String sqlQuery = getBillSummarySql(ucn, null, primary, status, neStatus);
        
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);            
        query.addEntity("agreement", Agreement.class);
        query.addEntity("agreement_term", AgreementTerm.class);
             
        @SuppressWarnings("unchecked")
		List<Object []> objects = query.list();

		return getAgreementSummaryList(objects, primary, false);
	}
	
	
	protected static String getBillSummarySql(int ucn, List<Integer> ucns, boolean primary, char status, char neStatus) throws Exception {
		/*
		 * IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT 
		 * 
		 * Because of the way Hibernate works, it is critical that the SQL below be constructed with {} around the column names, 
		 * and then the appropriate entity references be explicitly add where this query is used.  Without this, Hibernate gets confused about the
		 * column names so when the same name appears in both tables (such as STATUS and CREATED_DATETIME) then both table entities
		 * will get the values of the first occurrence of the column name (i.e. the value from the first table).
		 */
		
		String sqlQuery = "SELECT {agreement.*}, {agreement_term.*} FROM agreement, agreement_term WHERE ";
		if (ucn > 0)
			sqlQuery += " agreement.bill_ucn = " + ucn;
		else if (ucns != null && ucns.size() > 0) {
			sqlQuery += " agreement.bill_ucn in (";
			StringBuffer sb = new StringBuffer();
			for (Integer oneUcn : ucns) {
				if (sb.length() > 0)
					sb.append(",");
				sb.append(oneUcn);
			}
			sqlQuery += sb.toString();
		} else
			throw new Exception("Illegal arguments to construct agreement summary SQL");
        sqlQuery += " and agreement.id = agreement_term.agreement_id ";
        if (status != AppConstants.STATUS_ANY_NONE) {	// NOTE: Status restrictions apply to both the master agreement and the individual terms
        	sqlQuery += "and agreement.status = '" + status + "' ";
        	sqlQuery += "and agreement_term.status = '" + status + "' ";
        }
        if (neStatus != AppConstants.STATUS_ANY_NONE) {	// NOTE: Status restrictions apply to both the master agreement and the individual terms
        	sqlQuery += "and agreement.status <> '" + neStatus + "' ";
        	sqlQuery += "and agreement_term.status <> '" + neStatus + "' ";
        }
        if (primary)
        	sqlQuery += "and  agreement_term.primary = 'y' ";
        sqlQuery += " order by agreement.bill_ucn, agreement.id";
        
        return sqlQuery;
	}
	
	protected static String getSiteSummarySql(int ucn, List<Integer> ucns, boolean primary, char status, char neStatus) throws Exception {
		/*
		 * IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT 
		 * 
		 * Because of the way Hibernate works, it is critical that the SQL below be constructed with {} around the column names, 
		 * and then the appropriate entity references be explicitly add where this query is used.  Without this, Hibernate gets confused about the
		 * column names so when the same name appears in both tables (such as STATUS and CREATED_DATETIME) then both table entities
		 * will get the values of the first occurrence of the column name (i.e. the value from the first table).
		 */
		
		String sqlQuery = "SELECT {agreement.*}, {agreement_term.*} FROM agreement, agreement_term, agreement_site WHERE ";
		sqlQuery += " agreement_site.agreement_id = agreement.id ";
		sqlQuery += " and agreement_site.status = '" + AppConstants.STATUS_ACTIVE + "' "; 
		if (ucn > 0)
			sqlQuery += " and agreement_site.site_ucn = " + ucn;
		else if (ucns != null && ucns.size() > 0) {
			sqlQuery += " agreement_site.site_ucn in (";
			StringBuffer sb = new StringBuffer();
			for (Integer oneUcn : ucns) {
				if (sb.length() > 0)
					sb.append(",");
				sb.append(oneUcn);
			}
			sqlQuery += sb.toString();
		} else
			throw new Exception("Illegal arguments to construct agreement summary SQL");
        sqlQuery += " and agreement.id = agreement_term.agreement_id ";
        if (status != AppConstants.STATUS_ANY_NONE) {	// NOTE: Status restrictions apply to both the master agreement and the individual terms
        	sqlQuery += "and agreement.status = '" + status + "' ";
        	sqlQuery += "and agreement_term.status = '" + status + "' ";
        }
        if (neStatus != AppConstants.STATUS_ANY_NONE) {	// NOTE: Status restrictions apply to both the master agreement and the individual terms
        	sqlQuery += "and agreement.status <> '" + neStatus + "' ";
        	sqlQuery += "and agreement_term.status <> '" + neStatus + "' ";
        }
        if (primary)
        	sqlQuery += "and  agreement_term.primary = 'y' ";
        sqlQuery += " order by agreement.bill_ucn, agreement.id";
        
        return sqlQuery;
	}
	
	protected static SortedMap<Integer, AgreementSummaryInstance> getAgreementSummaryList(List<Object []> objects, boolean primary, boolean siteSearch) throws Exception {
		SortedMap<Integer, AgreementSummaryInstance> agreementMap = new TreeMap<Integer, AgreementSummaryInstance>();
        AgreementSummaryInstance instance = null;
        for (Object [] row : objects) {
        	if (row.length == 2 && row [0] instanceof Agreement && row [1] instanceof AgreementTerm) {
        		Agreement		agreement		= (Agreement) row [0];
        		AgreementTerm	agreementTerm	= (AgreementTerm) row [1];
        		if (agreementMap.containsKey(agreement.getId())) {
        			instance = agreementMap.get(agreement.getId());
        		} else {
        			instance = getAgreementSummaryInstance(agreement, primary, siteSearch);
        			agreementMap.put(instance.getId(), instance);
        		}
        		applyAgreementTerm(instance, agreementTerm);
        	} else
        		throw new Exception("Unexpected SQL result in getAgreementSummaryList");
        }
        
        return agreementMap;
        
//        List<AgreementSummaryInstance> results = new ArrayList<AgreementSummaryInstance>();
//        results.addAll(agreementMap.values());
//        
//        return results;
	}
	
	protected static AgreementSummaryInstance getAgreementSummaryInstance(Agreement agreement, boolean primary, boolean siteSearch) {
		AgreementSummaryInstance instance = new AgreementSummaryInstance();
		
		instance.setId(agreement.getId());
		instance.setBillUcn(agreement.getBillUcn());
		instance.setCommissionCode(agreement.getCommissionCode());
		instance.setAgreementTypeCode(agreement.getAgreementTypeCode());
		instance.setDeleteReasonCode(agreement.getDeleteReasonCode());
		instance.setStatus(agreement.getStatus());
		instance.setCreatedDate(agreement.getCreatedDatetime());
		instance.setFromPrimaryOnly(primary);
		instance.setFromSiteSearch(siteSearch);
		
		return instance;
	}
	
	protected static void applyAgreementTerm(AgreementSummaryInstance instance, AgreementTerm agreementTerm) {
		
		if (agreementTerm.getStartDate() != null) {
			if (instance.getFirstStartDate() == null || instance.getFirstStartDate().after(agreementTerm.getStartDate())) {
				instance.setFirstStartDate(agreementTerm.getStartDate());
			}
			if (instance.getLastStartDate() == null || instance.getLastStartDate().before(agreementTerm.getStartDate())) {
				instance.setLastStartDate(agreementTerm.getStartDate());
			}
		}

		if (agreementTerm.getEndDate() != null) {
			if (instance.getEndDate() == null || instance.getEndDate().before(agreementTerm.getEndDate())) {
				instance.setEndDate(agreementTerm.getEndDate());
			}
		}

		if (agreementTerm.getTerminateDate() != null) {
			if (instance.getTerminateDate() == null || instance.getTerminateDate().before(agreementTerm.getTerminateDate())) {
				instance.setTerminateDate(agreementTerm.getTerminateDate());
			}
		}
	}
	

	
	public static void setDescriptions(AgreementInstance agreement) {
		if (agreement == null)
			return;
		
		
		if (agreement.getAgreementLinkId() > 0) {
			AgreementLink agreementLink = DbAgreementLink.getById(agreement.getAgreementLinkId());
			if (agreementLink != null) {
				agreement.setAgreementLink(DbAgreementLink.getInstance(agreementLink));
				DbAgreementLink.setDescriptions(agreement.getAgreementLink());
			} else {
				agreement.setAgreementLink(AgreementLinkInstance.getUnknownInstance(agreement.getAgreementLinkId()));
			}
		} else
			agreement.setAgreementLink(AgreementLinkInstance.getEmptyInstance());
		
		
		if (agreement.getAgreementTypeCode() != null && agreement.getAgreementTypeCode().length() > 0) {
			AgreementType agreementType = DbAgreementType.getByCode(agreement.getAgreementTypeCode());
			if (agreementType != null) {
				agreement.setAgreementType(DbAgreementType.getInstance(agreementType));
			} else {
				agreement.setAgreementType(AgreementTypeInstance.getUnknownInstance(agreement.getAgreementTypeCode()));
			}
		} else
			agreement.setAgreementType(AgreementTypeInstance.getEmptyInstance());
		
		
		if (agreement.getCommissionCode() != null && agreement.getCommissionCode().length() > 0) {
			CommissionType commissionType = DbCommissionType.getByCode(agreement.getCommissionCode());
			if (commissionType != null) {
				agreement.setCommissionType(DbCommissionType.getInstance(commissionType));
			} else {
				agreement.setCommissionType(CommissionTypeInstance.getUnknownInstance(agreement.getCommissionCode()));
			}
		} else
			agreement.setCommissionType(CommissionTypeInstance.getEmptyInstance());
		
		
		if (agreement.getDeleteReasonCode() != null && agreement.getDeleteReasonCode().length() > 0) {
			DeleteReason deleteReason = DbDeleteReason.getByCode(agreement.getDeleteReasonCode());
			if (deleteReason != null) {
				agreement.setDeleteReason(DbDeleteReason.getInstance(deleteReason));
			} else {
				agreement.setDeleteReason(DeleteReasonInstance.getUnknownInstance(agreement.getDeleteReasonCode()));
			}
		} else
			agreement.setDeleteReason(DeleteReasonInstance.getEmptyInstance());
		
	}
}
