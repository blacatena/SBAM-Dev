package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.Product;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.ProductInstance;

/**
 * SBAM Product database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbProduct extends HibernateAccessor {
	
	static String objectName = Product.class.getSimpleName();
	
	public static Product getByCode(String productCode) {
		return (Product) getByField(objectName, "productCode", productCode, "description");
	}
	
	public static ProductInstance getInstance(Product dbInstance) {
		ProductInstance instance = new ProductInstance();
		instance.setProductCode(dbInstance.getProductCode());
		instance.setDescription(dbInstance.getDescription());
		instance.setShortName(dbInstance.getShortName());
		instance.setDefaultTermType(dbInstance.getDefaultTermType());
		instance.setDefaultCommissionCode(dbInstance.getDefaultCommissionCode());
		instance.setOrgPath(dbInstance.getOrgPath());
		instance.setSeq(dbInstance.getSeq());
		instance.setStatus(dbInstance.getStatus());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		
		return instance;
	}
	
	public static List<Product> findAll() {
		List<Object> results = findAll(objectName);
		List<Product> reasons = new ArrayList<Product>();
		for (int i = 0; i < results.size(); i++)
			reasons.add((Product) results.get(i));
		return reasons;
	}
	
	/**
	 * Find all product codes.
	 * @param productCode
	 * @param excludeStatus
	 *  Use 'X' to exclude all deleted products.
	 * @return
	 */
	public static List<Product> findByCategory(String productCode, char excludeStatus) {
		List<Object> results = findAll(objectName);
		List<Product> products = new ArrayList<Product>();
		for (int i = 0; i < results.size(); i++)
			products.add((Product) results.get(i));
		return products;
	}
	
	public static List<Product> findFiltered(String productCode, String description, String shortName, String defaultTermType, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (productCode != null && productCode.length() > 0)
            	crit.add(Restrictions.like("productCode", productCode));
            if (description != null && description.length() > 0)
            	crit.add(Restrictions.like("description", description));
            if (shortName != null && shortName.length() > 0)
            	crit.add(Restrictions.like("shortName", shortName));
            if (defaultTermType != null)
            	crit.add(Restrictions.like("defaultTermType", defaultTermType));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("description"));
            @SuppressWarnings("unchecked")
			List<Product> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<Product>();
	}
}
