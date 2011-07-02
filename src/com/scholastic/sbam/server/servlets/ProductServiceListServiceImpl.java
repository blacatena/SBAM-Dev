package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.scholastic.sbam.client.services.ProductServiceListService;
import com.scholastic.sbam.server.database.codegen.ProductService;
import com.scholastic.sbam.server.database.codegen.Service;
import com.scholastic.sbam.server.database.objects.DbProductService;
import com.scholastic.sbam.server.database.objects.DbService;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.ProductServiceTreeInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service to list product service assignments.
 * 
 * Note that this class can be generalized for other implementations by making ProductServiceTreeInstance an implementation that requires the
 * getters and setters for Description, Type, and Children.
 */
@SuppressWarnings("serial")
public class ProductServiceListServiceImpl extends TreeListServiceBase<ProductServiceTreeInstance> implements ProductServiceListService {
	
	@Override
	public List<ProductServiceTreeInstance> getProductServices(String productCode, LoadConfig loadConfig) throws IllegalArgumentException {
		
		authenticate("list product services", SecurityManager.ROLE_CONFIG);
		
		if (productCode == null || productCode.length() == 0)
			throw new IllegalArgumentException("Product code is a required argument.");
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		ProductServiceTreeInstance root = null;

		List<ProductServiceTreeInstance> list = new ArrayList<ProductServiceTreeInstance>();
		try {
			
			//	Find only undeleted product services
			List<ProductService> productServices = DbProductService.findByProduct(productCode, 'X');
			
			//	Create a hash set of the selected product services
			HashSet<String> selectedServices = new HashSet<String>();
			for (ProductService productService : productServices) {
				selectedServices.add(productService.getId().getServiceCode());
			}
			
			List<Service> services = DbService.findUndeleted();

			for (Service service : services) {
				ProductServiceTreeInstance instance = new ProductServiceTreeInstance();
				
				instance.setProductCode(productCode);
				instance.setServiceCode(service.getServiceCode());
				instance.setDescription(service.getDescription());
				instance.setType(ProductServiceTreeInstance.SERVICE);
				instance.setSelected(selectedServices.contains(service.getServiceCode()));
				
				root = addFolderTree(list, root, instance, service.getPresentationPath());
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return list;
	}

	@Override
	protected ProductServiceTreeInstance getTreeInstance() {
		return new ProductServiceTreeInstance();
	}
}
