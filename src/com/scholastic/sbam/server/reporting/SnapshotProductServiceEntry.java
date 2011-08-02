package com.scholastic.sbam.server.reporting;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.Product;
import com.scholastic.sbam.server.database.codegen.ProductService;
import com.scholastic.sbam.server.database.codegen.Service;
import com.scholastic.sbam.server.database.objects.DbProduct;
import com.scholastic.sbam.server.database.objects.DbProductService;
import com.scholastic.sbam.server.database.objects.DbService;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * Utility class for server side snapshot compilation.  This class provides a consolidated map of all products and associated services.
 * 
 * @author Bob Lacatena
 *
 */
public class SnapshotProductServiceEntry {
	
	protected Product product;
	protected HashMap<String, Service> services = new HashMap<String, Service>();
	
	public SnapshotProductServiceEntry(Product product) {
		this.product = product;
	}
	
	public void add(Service service) {
		services.put(service.getServiceCode(), service);
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public HashMap<String, Service> getServices() {
		return services;
	}

	public void setServices(HashMap<String, Service> services) {
		this.services = services;
	}

	public Collection<Service> getServiceList() {
		return services.values();
	}
	
	public double getFraction(String serviceCode) {
		//	If no services, or not asking for a particular service, return 1
		if (services.size() == 0 || serviceCode == null || serviceCode.length() == 0)
			return 1.0d;
		//	If this service isn't in this product, it gets no share
		if (!services.containsKey(serviceCode))
			return 0.0d;
		//	For now, this just allocates all services evenly (until database modifications are made to distribute by product service defined weight)
		return 1.0d / services.size();
	}
	
	public boolean hasService(String serviceCode) {
		return services.containsKey(serviceCode);
	}
	
	public String toString() {
		return product.getProductCode() + " : " + services.keySet();
	}
	
	/**
	 * Return a map of all active products and services.
	 * @return
	 */
	public static HashMap<String, SnapshotProductServiceEntry> getProductServicesMap() {
		boolean localTransaction = !HibernateUtil.isTransactionInProgress();
		
		if (localTransaction) {
			HibernateUtil.openSession();
			HibernateUtil.startTransaction();
		}
		
		HashMap<String, SnapshotProductServiceEntry>  map = new HashMap<String, SnapshotProductServiceEntry>();
		
		List<Product> products = DbProduct.findAll(); // findFiltered(null, null, null, null, AppConstants.STATUS_ACTIVE, AppConstants.STATUS_DELETED);
		List<ProductService> productServices = DbProductService.findFiltered(null, null, AppConstants.STATUS_ACTIVE, AppConstants.STATUS_DELETED);
		List<Service> services = DbService.findAll(); // findFiltered(null, null, (char) 0, null, AppConstants.STATUS_ACTIVE, AppConstants.STATUS_DELETED);
		
		//	A map of services, to look them up quickly
		HashMap<String, Service> serviceMap = new HashMap<String, Service>();
		for (Service service : services)
			serviceMap.put(service.getServiceCode(), service);
		
		//	The basic product/service entry map, to look them up quickly
		for (Product product : products) {
			map.put(product.getProductCode(), new SnapshotProductServiceEntry(product));
		}
		
		//	Now create the full map, for use in compilation, and return it
		for (ProductService productService : productServices) {
			
			Service service = serviceMap.get(productService.getId().getServiceCode());
			
			if (service == null) {
				System.out.println(productService.getId().getProductCode() + "-" + productService.getId().getServiceCode());
				System.out.println(map);
				throw new IllegalArgumentException("INTERNAL ERROR: Service " + productService.getId().getServiceCode() + " referenced in product services table is missing.");
			}
			
			if (service.getStatus() != AppConstants.STATUS_ACTIVE)
				continue;
			
			if (map.containsKey(productService.getId().getProductCode())) {
				map.get(productService.getId().getProductCode()).add(service);
			} else {
				System.out.println(productService.getId().getProductCode() + "-" + productService.getId().getServiceCode());
				System.out.println(map);
				throw new IllegalArgumentException("INTERNAL ERROR: Product " + productService.getId().getProductCode() + " referenced in product services table is missing.");
			}
		}
		
		if (localTransaction) {
			HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return map;
	}
}
