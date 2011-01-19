package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.scholastic.sbam.client.services.ProductListService;
import com.scholastic.sbam.server.database.codegen.Product;
import com.scholastic.sbam.server.database.objects.DbProduct;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.ProductInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ProductListServiceImpl extends AuthenticatedServiceServlet implements ProductListService {

	@Override
	public List<ProductInstance> getProducts(LoadConfig loadConfig) throws IllegalArgumentException {
		
		authenticate("list products", SecurityManager.ROLE_CONFIG);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<ProductInstance> list = new ArrayList<ProductInstance>();
		try {
			
			//	Find only undeleted delete reasons
			List<Product> products = DbProduct.findFiltered(null, null, null, (char) 0, (char) 0, 'X');

			for (Product product : products) {
				ProductInstance instance = new ProductInstance();
				instance.setProductCode(product.getProductCode());
				instance.setDescription(product.getDescription());
				instance.setShortName(product.getShortName());
				instance.setDefaultTermType(product.getDefaultTermType());
				instance.setStatus(product.getStatus());
				instance.setCreatedDatetime(product.getCreatedDatetime());
				list.add(instance);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return list;
	}
}
