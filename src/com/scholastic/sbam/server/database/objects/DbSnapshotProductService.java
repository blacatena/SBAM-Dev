package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.SnapshotProductService;
import com.scholastic.sbam.server.database.codegen.SnapshotProductServiceId;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbSnapshotProductService extends HibernateAccessor {
	
	static String objectName = SnapshotProductService.class.getSimpleName();
	
	public static SnapshotProductService getById(String snapshotCode, String productServiceCode) {
		SnapshotProductServiceId spsId = new SnapshotProductServiceId();
		spsId.setSnapshotCode(snapshotCode);
		spsId.setProductServiceCode(productServiceCode);
		
		return getById(spsId);
	}
	
	public static SnapshotProductService getById(SnapshotProductServiceId spsId) {
		SnapshotProductService snapshotProductService = (SnapshotProductService) sessionFactory.getCurrentSession().get(getObjectReference(objectName), spsId);
		return snapshotProductService;
	}
	
	public static List<SnapshotProductService> findAll() {
		List<Object> results = findAll(objectName);
		List<SnapshotProductService> reasons = new ArrayList<SnapshotProductService>();
		for (int i = 0; i < results.size(); i++)
			reasons.add((SnapshotProductService) results.get(i));
		return reasons;
	}
	
	/**
	 * Find all services for a snapshot.
	 * @param snapshotCode
	 * @param excludeStatus
	 *  Use 'X' to exclude all deleted service codes.
	 * @return
	 */
	public static List<SnapshotProductService> findServiceBySnapshot(String snapshotCode, char excludeStatus) {
		return findFilteredService(snapshotCode, null, excludeStatus);
	}
	
	/**
	 * Find all services for a snapshot.
	 * @param snapshotCode
	 * @param excludeStatus
	 *  Use 'X' to exclude all deleted service codes.
	 * @return
	 */
	public static List<SnapshotProductService> findProductBySnapshot(String snapshotCode, char excludeStatus) {
		return findFilteredProduct(snapshotCode, null, excludeStatus);
	}
	
	public static List<SnapshotProductService> findFiltered(String snapshotCode, String productServiceCode) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (snapshotCode != null && snapshotCode.length() > 0)
            	crit.add(Restrictions.like("id.snapshotCode", snapshotCode));
            if (productServiceCode != null && productServiceCode.length() > 0)
            	crit.add(Restrictions.like("id.productServiceCode", productServiceCode));
            crit.addOrder(Order.asc("id.snapshotCode"));
            crit.addOrder(Order.asc("id.productServiceCode"));
            @SuppressWarnings("unchecked")
			List<SnapshotProductService> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<SnapshotProductService>();
	}
	
	public static List<SnapshotProductService> findFilteredService(String snapshotCode, String serviceCode, char neStatus) {
		return findFilteredService(snapshotCode, serviceCode, AppConstants.STATUS_ANY_NONE, neStatus);
	}
	
	public static List<SnapshotProductService> findFilteredService(String snapshotCode, String serviceCode, char status, char neStatus) {
        try
        {
        	if (neStatus == 0)
        		return findFiltered(snapshotCode, serviceCode);
        	
        	String sqlQuery = "SELECT snapshot_product_service.* FROM snapshot_product_service, service WHERE ";
            if (snapshotCode != null && snapshotCode.length() > 0)
            	sqlQuery += " snapshot_product_service.snapshot_code = '" + snapshotCode + "' AND ";
            if (serviceCode != null && serviceCode.length() > 0)
            	sqlQuery += " snapshot_product_service.product_service_code = '" + serviceCode + "' AND ";
            sqlQuery += " service.service_code = snapshot_product_service.product_service_code AND ";
            if (status != AppConstants.STATUS_ANY_NONE) {
                sqlQuery += " service.status = '" + status + "' AND ";
            }
            if (neStatus == AppConstants.STATUS_ANY_NONE)
            	neStatus = AppConstants.STATUS_DELETED;
            sqlQuery += " service.status <> '" + neStatus + "' ";
            sqlQuery += " order by snapshot_product_service.snapshot_code, snapshot_product_service.product_service_code";
            
            SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
            
            query.addEntity(getObjectReference(objectName));
            
            @SuppressWarnings("unchecked")
			List<SnapshotProductService> objects = query.list();
            
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<SnapshotProductService>();
	}
	
	public static List<SnapshotProductService> findFilteredProduct(String snapshotCode, String productCode, char neStatus) {
		return findFilteredService(snapshotCode, productCode, AppConstants.STATUS_ANY_NONE, neStatus);
	}
	
	public static List<SnapshotProductService> findFilteredProduct(String snapshotCode, String productCode, char status, char neStatus) {
        try
        {
        	if (neStatus == 0)
        		return findFiltered(snapshotCode, productCode);
        	
        	String sqlQuery = "SELECT snapshot_product_service.* FROM snapshot_product_service, product WHERE ";
            if (snapshotCode != null && snapshotCode.length() > 0)
            	sqlQuery += " snapshot_product_service.snapshot_code = '" + snapshotCode + "' AND ";
            if (productCode != null && productCode.length() > 0)
            	sqlQuery += " snapshot_product_service.product_service_code = '" + productCode + "' AND ";
            sqlQuery += " product.product_code = snapshot_product_service.product_service_code AND ";
            if (status != AppConstants.STATUS_ANY_NONE) {
                sqlQuery += " product.status = '" + status + "' AND ";
            }
            if (neStatus == AppConstants.STATUS_ANY_NONE)
            	neStatus = AppConstants.STATUS_DELETED;
            sqlQuery += " product.status <> '" + neStatus + "' ";
            sqlQuery += " order by snapshot_product_service.snapshot_code, snapshot_product_service.product_service_code";
            
            SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
            
            query.addEntity(getObjectReference(objectName));
            
            @SuppressWarnings("unchecked")
			List<SnapshotProductService> objects = query.list();
            
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<SnapshotProductService>();
	}
}
