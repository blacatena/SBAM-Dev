package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.ProductService;
import com.scholastic.sbam.server.database.util.HibernateAccessor;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbProductService extends HibernateAccessor {
	
	static String objectName = ProductService.class.getSimpleName();
	
	public static ProductService getByCode(String productCode, String serviceCode) {
		List<ProductService> list = findFiltered(productCode, serviceCode, (char) 0);
		if (list == null || list.size() == 0)
			return null;
		return list.get(0);
	}
	
	public static List<ProductService> findAll() {
		List<Object> results = findAll(objectName);
		List<ProductService> reasons = new ArrayList<ProductService>();
		for (int i = 0; i < results.size(); i++)
			reasons.add((ProductService) results.get(i));
		return reasons;
	}
	
	/**
	 * Find all product services for a product.
	 * @param productCode
	 * @param excludeStatus
	 *  Use 'X' to exclude all deleted service codes.
	 * @return
	 */
	public static List<ProductService> findByProduct(String productCode, char excludeStatus) {
		return findFiltered(productCode, null, excludeStatus);
	}
	
	public static List<ProductService> findFiltered(String productCode, String serviceCode) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (productCode != null && productCode.length() > 0)
            	crit.add(Restrictions.like("id.productCode", productCode));
            if (serviceCode != null && serviceCode.length() > 0)
            	crit.add(Restrictions.like("id.serviceCode", serviceCode));
            crit.addOrder(Order.asc("id.productCode"));
            crit.addOrder(Order.asc("id.serviceCode"));
            System.out.println(crit.toString());
            @SuppressWarnings("unchecked")
			List<ProductService> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<ProductService>();
	}
	
	public static List<ProductService> findFiltered(String productCode, String serviceCode, char neStatus) {
        try
        {
        	if (neStatus == 0)
        		return findFiltered(productCode, serviceCode);
        	
        	String sqlQuery = "SELECT product_service.* FROM product_service, service WHERE ";
            if (productCode != null && productCode.length() > 0)
            	sqlQuery += " product_service.product_code = '" + productCode + "' AND ";
            if (serviceCode != null && serviceCode.length() > 0)
            	sqlQuery += " product_service.service_code = '" + serviceCode + "' AND ";
            sqlQuery += " service.service_code = product_service.service_code AND ";
            sqlQuery += " service.status <> '" + neStatus + "' ";
            sqlQuery += " order by product_service.product_code, product_service.service_code";
            
            SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
            
            query.addEntity(getObjectReference(objectName));
            
            @SuppressWarnings("unchecked")
			List<ProductService> objects = query.list();
            
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<ProductService>();
	}
}
