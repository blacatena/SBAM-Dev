package com.scholastic.sbam.server.database.objects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.SnapshotTermData;
import com.scholastic.sbam.server.database.codegen.SnapshotTermDataId;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.SnapshotTermDataInstance;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbSnapshotTermData extends HibernateAccessor {
	
	static String objectName = SnapshotTermData.class.getSimpleName();
	
	public static SnapshotTermData getById(int snapshotId, int agreementId, int termId, String productCode, String serviceCode, int ucn, int ucnSuffix, int rowId) {
		SnapshotTermDataId stdId = new SnapshotTermDataId();
		stdId.setSnapshotId(snapshotId);
		stdId.setAgreementId(agreementId);
		stdId.setTermId(termId);
		stdId.setProductCode(productCode);
		stdId.setServiceCode(serviceCode);
		stdId.setUcn(ucn);
		stdId.setUcnSuffix(ucnSuffix);
		stdId.setRowId(rowId);
		
		return getById(stdId);
	}
	
	public static SnapshotTermData getById(SnapshotTermDataId spsId) {
		SnapshotTermData snapshotTermData = (SnapshotTermData) sessionFactory.getCurrentSession().get(getObjectReference(objectName), spsId);
		return snapshotTermData;
	}
	
	public static List<SnapshotTermData> findAll() {
		List<Object> results = findAll(objectName);
		List<SnapshotTermData> reasons = new ArrayList<SnapshotTermData>();
		for (int i = 0; i < results.size(); i++)
			reasons.add((SnapshotTermData) results.get(i));
		return reasons;
	}
	
	public static List<SnapshotTermData> findFiltered(int snapshotId, int agreementId, int termId, String productCode, String serviceCode, int ucn, int ucnSuffix, List<Order> sorts) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (snapshotId > 0)
            	crit.add(Restrictions.like("id.snapshotId", snapshotId));
            if (agreementId > 0)
            	crit.add(Restrictions.like("id.agreementId", agreementId));
            if (termId > 0)
            	crit.add(Restrictions.like("id.termId", termId));
            if (productCode != null && productCode.length() > 0)
            	crit.add(Restrictions.like("id.productCode", productCode));
            if (serviceCode != null && serviceCode.length() > 0)
            	crit.add(Restrictions.like("id.serviceCode", serviceCode));
            if (ucn > 0)
            	crit.add(Restrictions.like("id.ucn", ucn));
            if (ucnSuffix > 0)
            	crit.add(Restrictions.like("id.ucnSuffix", ucnSuffix));
            
            if (sorts != null) {
            	for (Order sort : sorts)
            		crit.addOrder(sort);
            } else {
	            crit.addOrder(Order.asc("id.snapshotId"));
	            crit.addOrder(Order.asc("startDate"));
	            crit.addOrder(Order.asc("endDate"));
	            crit.addOrder(Order.asc("id.serviceCode"));
            }
            
            @SuppressWarnings("unchecked")
			List<SnapshotTermData> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<SnapshotTermData>();
	}

	public static int getNextRowId(int snapshotId, int agreementId, int termId, String productCode, String serviceCode, int ucn, int ucnSuffix) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
        crit.add(Restrictions.eq("id.snapshotId",			snapshotId));
        crit.add(Restrictions.eq("id.agreementId",			agreementId));
        crit.add(Restrictions.eq("id.termId",				termId));
        crit.add(Restrictions.eq("id.productCode",			productCode));
        crit.add(Restrictions.eq("id.serviceCode",			serviceCode));
        crit.add(Restrictions.eq("id.ucn",					ucn));
        crit.add(Restrictions.eq("id.ucnSuffix",			ucnSuffix));
        crit.setMaxResults(1);
        crit.addOrder(Order.desc("id.rowId"));
        @SuppressWarnings("unchecked")
		List<SnapshotTermData> objects = crit.list();
        if (objects == null || objects.size() == 0)
        	return 1;
        return objects.get(0).getId().getRowId() + 1;
	}
	
	public static int deleteAll(int snapshotId) {
		String hql = "delete from SnapshotTermData snapshotTermData where id.snapshotId = " + snapshotId;
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		return query.executeUpdate();
	}
	
	public static SnapshotTermDataInstance getInstance(SnapshotTermData dbTermData) throws SQLException {
		SnapshotTermDataInstance instance = new SnapshotTermDataInstance();
		
		instance.setSnapshotId(dbTermData.getId().getSnapshotId());
		instance.setAgreementId(dbTermData.getId().getAgreementId());
		instance.setTermId(dbTermData.getId().getTermId());
		instance.setRowId(dbTermData.getId().getRowId());
		instance.setProductCode(dbTermData.getId().getProductCode());
		instance.setServiceCode(dbTermData.getId().getServiceCode());
		instance.setUcn(dbTermData.getId().getUcn());
		instance.setUcnSuffix(dbTermData.getId().getUcnSuffix());
		instance.setStartDate(dbTermData.getStartDate());
		instance.setEndDate(dbTermData.getEndDate());
		instance.setTerminateDate(dbTermData.getTerminateDate());
		instance.setDollarValue(dbTermData.getDollarValue().doubleValue());
		instance.setCancelReasonCode(dbTermData.getCancelReasonCode());
		instance.setCancelDate(dbTermData.getCancelDate());
		instance.setCommissionCode(dbTermData.getCommissionCode());
		instance.setTermType(dbTermData.getTermType());
		instance.setPrimaryTerm(dbTermData.getPrimaryTerm());
		instance.setWorkStations(dbTermData.getWorkstations());
		instance.setBuildings(dbTermData.getBuildings());
		instance.setPopulation(dbTermData.getPopulation());
		instance.setEnrollment(dbTermData.getEnrollment());
		
		return instance;
	}
	
	public static SnapshotTermDataInstance getInstance(ResultSet result) throws SQLException {
		SnapshotTermDataInstance instance = new SnapshotTermDataInstance();
		
		instance.setSnapshotId(result.getInt("SNAPSHOT_ID"));
		instance.setAgreementId(result.getInt("AGREEMENT_ID"));
		instance.setTermId(result.getInt("TERM_ID"));
		instance.setStartDate(result.getDate("START_DATE"));
		instance.setEndDate(result.getDate("END_DATE"));
		instance.setTerminateDate(result.getDate("TERMINATE_DATE"));
		instance.setDollarValue(result.getDouble("DOLLAR_VALUE"));
		instance.setCancelReasonCode(result.getString("CANCEL_REASON_CODE"));
		instance.setCancelDate(result.getDate("CANCEL_DATE"));
		instance.setCommissionCode(result.getString("COMMISSION_CODE"));
		instance.setTermType(result.getString("TERM_TYPE"));
		instance.setPrimaryTerm(result.getString("PRIMARY_TERM").charAt(0));
		instance.setWorkStations(result.getInt("WORKSTATIONS"));
		instance.setBuildings(result.getInt("BUILDINGS"));
		instance.setPopulation(result.getInt("POPULATION"));
		instance.setEnrollment(result.getInt("ENROLLMENT"));
		
		//	These are allowed to fail... a result set may or may not have them
		try { instance.setProductCode(result.getString("PRODUCT_CODE")); } catch (SQLException exc) { instance.setProductCode(""); }
		try { instance.setServiceCode(result.getString("SERVICE_CODE")); } catch (SQLException exc) { instance.setServiceCode(""); }
		try { instance.setUcn(result.getInt("UCN")); } catch (SQLException exc) { instance.setUcn(0); }
		try { instance.setUcnSuffix(result.getInt("UCN_SUFFIX")); } catch (SQLException exc) { instance.setUcnSuffix(0); }
		try { instance.setRowId(result.getInt("ROW_ID")); } catch (SQLException exc) { instance.setRowId(0); }
		
		return instance;
	}
}
