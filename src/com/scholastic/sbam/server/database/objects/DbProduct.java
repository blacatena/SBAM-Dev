package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.Product;
import com.scholastic.sbam.server.database.util.HibernateAccessor;

/**
 * SBAM Product database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbProduct extends HibernateAccessor {
	
	static String objectName = Product.class.getSimpleName();
	
	public static Product getByUserName(String userName) {
		return (Product) getByField(objectName, "productCode", userName, "name");
	}
	
	public static List<Product> findByName(String lastName) {
		List<Object> results = findByField(objectName, "name", lastName, "name");
		List<Product> products = new ArrayList<Product>();
		for (int i = 0; i < results.size(); i++)
			products.add((Product) results.get(i));
		return products;
	}
	
	public static List<Product> findAll() {
		List<Object> results = findAll(objectName);
		List<Product> products = new ArrayList<Product>();
		for (int i = 0; i < results.size(); i++)
			products.add((Product) results.get(i));
		return products;
	}
}
