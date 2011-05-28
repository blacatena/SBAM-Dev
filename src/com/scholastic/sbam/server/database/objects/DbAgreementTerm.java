package com.scholastic.sbam.server.database.objects;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.AgreementTerm;
import com.scholastic.sbam.server.database.codegen.AgreementTermId;
import com.scholastic.sbam.server.database.codegen.CancelReason;
import com.scholastic.sbam.server.database.codegen.CommissionType;
import com.scholastic.sbam.server.database.codegen.Product;
import com.scholastic.sbam.server.database.codegen.TermType;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;
import com.scholastic.sbam.shared.objects.CancelReasonInstance;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.objects.ProductInstance;
import com.scholastic.sbam.shared.objects.TermTypeInstance;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbAgreementTerm extends HibernateAccessor {
	
	static String objectName = AgreementTerm.class.getSimpleName();
	
	public static AgreementTermInstance getInstance(AgreementTerm dbInstance) {
		AgreementTermInstance instance = new AgreementTermInstance();

		instance.setAgreementId(dbInstance.getId().getAgreementId());
		instance.setTermId(dbInstance.getId().getTermId());
		
		instance.setProductCode(dbInstance.getProductCode());
		instance.setStartDate(dbInstance.getStartDate());
		instance.setEndDate(dbInstance.getEndDate());
		instance.setTerminateDate(dbInstance.getTerminateDate());
		instance.setTermTypeCode(dbInstance.getTermType());
		
		instance.setCommissionCode(dbInstance.getCommissionCode());
		instance.setCancelReasonCode(dbInstance.getCancelReasonCode());
		instance.setCancelDate(dbInstance.getCancelDate());
		
		instance.setDollarValue(dbInstance.getDollarValue().doubleValue());
		instance.setWorkstations(dbInstance.getWorkstations());
		instance.setBuildings(dbInstance.getBuildings());
		instance.setPopulation(dbInstance.getPopulation());
		instance.setEnrollment(dbInstance.getEnrollment());
		
		instance.setPoNumber(dbInstance.getPoNumber());
		instance.setReferenceSaId(dbInstance.getReferenceSaId());
		
		instance.setPrimary(dbInstance.getPrimaryTerm());
		instance.setOrgPath(dbInstance.getOrgPath());
		instance.setPrimaryOrgPath(dbInstance.getPrimaryOrgPath());
		instance.setNote(dbInstance.getNote());
		
		instance.setStatus(dbInstance.getStatus());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		
		return instance;
	}
	
	public static AgreementTerm getById(int agreementId, int termId) {
		AgreementTermId aid = new AgreementTermId();
		aid.setAgreementId(agreementId);
		aid.setTermId(termId);
		try {
			AgreementTerm instance = (AgreementTerm) sessionFactory.getCurrentSession().get(getObjectReference(objectName), aid);
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
	
	public static List<AgreementTerm> findAll() {
		List<Object> results = findAll(objectName);
		List<AgreementTerm> reasons = new ArrayList<AgreementTerm>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((AgreementTerm) results.get(i));
		return reasons;
	}
	
	public static List<AgreementTerm> findByAgreementId(int agreementId, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (agreementId > 0)
            	crit.add(Restrictions.eq("id.agreementId", agreementId));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("orgPath"));
            crit.addOrder(Order.asc("endDate"));
            @SuppressWarnings("unchecked")
			List<AgreementTerm> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<AgreementTerm>();
	}

	public static int getNextTermId(int agreementId) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
        crit.add(Restrictions.eq("id.agreementId", agreementId));
        crit.setMaxResults(1);
        crit.addOrder(Order.desc("id.termId"));
        @SuppressWarnings("unchecked")
		List<AgreementTerm> objects = crit.list();
        if (objects == null || objects.size() == 0)
        	return 1;
        return objects.get(0).getId().getTermId() + 1;
	}
	
	public static List<Object []> findFiltered(String filter, char neStatus) {
		return findFiltered(filter, null, null, null, neStatus);
	}
	
	public static List<Object []> findFiltered(String filter, String dateType, Date fromDate, Date toDate, char neStatus) {
        try
        {
        	if (filter == null || filter.trim().length() == 0 || dateType == null || fromDate == null || toDate == null)
        		return new ArrayList<Object []>();
        	
        	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        	String fromDateStr = "'" + format.format(fromDate) + "'";	//fromDate.getDate() + "-" + fromDate.getMonth() + "-" + fromDate.getYear();
        	String toDateStr = "'" + format.format(toDate) + "'";		//toDate.getDate() + "-" + toDate.getMonth() + "-" + toDate.getYear();
        		
        	AppConstants.TypedTerms typedTerms = AppConstants.parseTypedFilterTerms(filter);
        		
        	String sqlQuery = "SELECT {agreement.*}, {agreement_term.*}, {product.*} FROM agreement, agreement_term, product WHERE agreement.`status` <> '" + neStatus + "' " +
        						" AND agreement_term.status <> '" + neStatus + "' " +
        						" AND agreement.id = agreement_term.agreement_id " + 
        						" AND agreement_term.product_code = product.product_code ";
        	
        	if (dateType != null) {
        		if ("start".equalsIgnoreCase(dateType)) {
        			sqlQuery += " AND start_date >= " + fromDateStr;
        			sqlQuery += " AND start_date <= " + toDateStr + " ";
        		} else if ("end".equalsIgnoreCase(dateType)) {
        			sqlQuery += " AND end_date >= " + fromDateStr;
        			sqlQuery += " AND end_date <= " + toDateStr + " ";
        		} else if ("terminate".equalsIgnoreCase(dateType)) {
        			sqlQuery += " AND terminate_date >= " + fromDateStr;
        			sqlQuery += " AND terminate_date <= " + toDateStr + " ";
        		} else if ("within".equalsIgnoreCase(dateType)) {
        			sqlQuery += " AND start_date >= " + fromDateStr;
        			sqlQuery += " AND (end_date is null || end_date <= " + toDateStr + ") ";
        		}
        	}
        	
        	if (typedTerms.getNumbers().size() > 0) {
	        	sqlQuery += " AND ( ";
	        	for (int i = 0; i < typedTerms.getNumbers().size(); i++) {
	        		if (i > 0) sqlQuery += " OR ";
	        		String numberLike = " like '%" + typedTerms.getNumbers().get(i) + "%' ";
	        		sqlQuery += " agreement.id" + numberLike;
	        		sqlQuery += " OR agreement.id_check_digit" + numberLike;
	        		sqlQuery += " OR agreement_term.dollar_value" + numberLike;
	        		sqlQuery += " OR agreement_term.note" + numberLike;
	        	}
	        	sqlQuery += " ) ";
        	}
        	
        	if (typedTerms.getWords().size() > 0) {
        		sqlQuery += " AND ( ";
        		for (int i = 0; i < typedTerms.getWords().size(); i++) {
        			if (i > 0) sqlQuery += " OR ";
        			String wordLike = " like '%" + typedTerms.getWords().get(i) + "%' ";
        			sqlQuery += " agreement_term.note" + wordLike;
        			sqlQuery += " OR product.description" + wordLike;
        			//	Only apply it to product code if it's a word, not a phrase (i.e. no blanks)
        			if (typedTerms.getWords().get(i).indexOf(' ') < 0)
        				sqlQuery += " OR product.product_code" + wordLike;
        		}
        		sqlQuery += " ) ";
        	}
        	
 //       	System.out.println(sqlQuery);
        	
            sqlQuery += " order by agreement_term.agreement_id, agreement_term.start_date, agreement_term.end_date, product.description";
            
            SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
            
            query.addEntity("agreement",		getObjectReference("Agreement"));
            query.addEntity("agreement_term",	getObjectReference("AgreementTerm"));
            query.addEntity("product",			getObjectReference("Product"));
            
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
	
	public static List<Object []> findActive(int agreementId) {
        try
        {
        	if (agreementId <= 0)
        		return new ArrayList<Object []>();
        	
        	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        	String todayDateStr = "'" + format.format(new Date()) + "'";
        		
        	StringBuffer sqlQuery = new StringBuffer();
        	
        	sqlQuery.append("SELECT {agreement.*}, {agreement_term.*}, {product.*} FROM agreement, agreement_term, product ");
        	sqlQuery.append(" WHERE agreement.id = ");
        	sqlQuery.append(agreementId);
        	sqlQuery.append(" AND agreement.status <> '");
        	sqlQuery.append(AppConstants.STATUS_DELETED);
        	sqlQuery.append("'");
        	sqlQuery.append(" AND agreement.status <> '");
        	sqlQuery.append(AppConstants.STATUS_INACTIVE);
        	sqlQuery.append("'");
        	sqlQuery.append(" AND agreement_term.status <> '");
        	sqlQuery.append(AppConstants.STATUS_DELETED);
        	sqlQuery.append("'");
        	sqlQuery.append(" AND agreement_term.status <> '");
        	sqlQuery.append(AppConstants.STATUS_INACTIVE);
        	sqlQuery.append("'");
        	sqlQuery.append(" AND product.status <> '");
        	sqlQuery.append(AppConstants.STATUS_DELETED);
        	sqlQuery.append("'");
        	sqlQuery.append(" AND product.status <> '");
        	sqlQuery.append(AppConstants.STATUS_INACTIVE);
        	sqlQuery.append("'");
        	sqlQuery.append(" AND agreement.id = agreement_term.agreement_id ");
        	sqlQuery.append(" AND agreement_term.product_code = product.product_code ");
        	
        	sqlQuery.append(" AND start_date <= ");
        	sqlQuery.append(todayDateStr);
        	sqlQuery.append(" AND (terminate_date >= ");
        	sqlQuery.append(todayDateStr);
        	sqlQuery.append(" OR end_date >= ");
        	sqlQuery.append(todayDateStr);
        	sqlQuery.append(")");
        	
        	sqlQuery.append(" order by agreement_term.agreement_id, agreement_term.start_date, agreement_term.end_date, product.description");
        	
//        	System.out.println(sqlQuery);
        	
            SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery.toString());
            
            query.addEntity("agreement",		getObjectReference("Agreement"));
            query.addEntity("agreement_term",	getObjectReference("AgreementTerm"));
            query.addEntity("product",			getObjectReference("Product"));
            
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
	
	public static void setDescriptions(AgreementTermInstance agreementTerm) {
		if (agreementTerm == null)
			return;
		
		if (agreementTerm.getTermTypeCode() != null) {
			TermType tType = DbTermType.getByCode(agreementTerm.getTermTypeCode());
			if (tType != null) {
				agreementTerm.setTermType(DbTermType.getInstance(tType));
			} else {
				agreementTerm.setTermType(TermTypeInstance.getUnknownInstance(agreementTerm.getTermTypeCode()));
			}
		} else
			agreementTerm.setTermType(TermTypeInstance.getUnknownInstance("none"));
		
		
		if (agreementTerm.getCommissionCode() != null && agreementTerm.getCommissionCode().length() > 0) {
			CommissionType commissionType = DbCommissionType.getByCode(agreementTerm.getCommissionCode());
			if (commissionType != null) {
				agreementTerm.setCommissionType(DbCommissionType.getInstance(commissionType));
			} else {
				agreementTerm.setCommissionType(CommissionTypeInstance.getUnknownInstance(agreementTerm.getCommissionCode()));
			}
		} else
			agreementTerm.setCommissionType(CommissionTypeInstance.getEmptyInstance());
		
		
		if (agreementTerm.getCancelReasonCode() != null && agreementTerm.getCancelReasonCode().length() > 0) {
			CancelReason cancelReason = DbCancelReason.getByCode(agreementTerm.getCancelReasonCode());
			if (cancelReason != null) {
				agreementTerm.setCancelReason(DbCancelReason.getInstance(cancelReason));
			} else {
				agreementTerm.setCancelReason(CancelReasonInstance.getUnknownInstance(agreementTerm.getCancelReasonCode()));
			}
		} else
			agreementTerm.setCancelReason(CancelReasonInstance.getEmptyInstance());
			
		
		if (agreementTerm.getProductCode() != null && agreementTerm.getProductCode().length() > 0) {
			//	Optimization, in case product has already been properly set through another query
			if (agreementTerm.getProduct() == null || !agreementTerm.getProductCode().equals(agreementTerm.getProduct().getProductCode())) {
				Product product = DbProduct.getByCode(agreementTerm.getProductCode());
				if (product != null) {
					agreementTerm.setProduct(DbProduct.getInstance(product));
				} else {
					agreementTerm.setProduct(ProductInstance.getUnknownInstance(agreementTerm.getProductCode()));
				}
			}
		} else
			agreementTerm.setProduct(ProductInstance.getEmptyInstance());
		
	}
}
