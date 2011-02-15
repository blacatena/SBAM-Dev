package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.scholastic.sbam.client.services.ProductListService;
import com.scholastic.sbam.server.database.codegen.CommissionType;
import com.scholastic.sbam.server.database.codegen.Product;
import com.scholastic.sbam.server.database.codegen.TermType;
import com.scholastic.sbam.server.database.objects.DbCommissionType;
import com.scholastic.sbam.server.database.objects.DbProduct;
import com.scholastic.sbam.server.database.objects.DbTermType;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.objects.ProductInstance;
import com.scholastic.sbam.shared.objects.TermTypeInstance;
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
			List<TermType> termTypeList = DbTermType.findAll();
			HashMap<String, TermTypeInstance> termTypes = new HashMap<String, TermTypeInstance>();
			for (TermType dbTermType : termTypeList) {
				termTypes.put(dbTermType.getTermTypeCode().toLowerCase(), DbTermType.getInstance(dbTermType));
			}
			List<CommissionType> commTypeList = DbCommissionType.findAll();
			HashMap<String, CommissionTypeInstance> commTypes = new HashMap<String, CommissionTypeInstance>();
			for (CommissionType dbCommType : commTypeList) {
				commTypes.put(dbCommType.getCommissionCode().toLowerCase(), DbCommissionType.getInstance(dbCommType));
			}
			
			//	Find only undeleted delete reasons
			List<Product> products = DbProduct.findFiltered(null, null, null, null, (char) 0, 'X');

			for (Product product : products) {
				ProductInstance instance = new ProductInstance();
				instance.setProductCode(product.getProductCode());
				instance.setDescription(product.getDescription());
				instance.setShortName(product.getShortName());
				instance.setDefaultTermType(product.getDefaultTermType());
				instance.setStatus(product.getStatus());
				instance.setCreatedDatetime(product.getCreatedDatetime());
				
				instance.setDefaultTermTypeInstance(null);
				if (termTypes.containsKey(product.getDefaultTermType().toLowerCase())) {
					instance.setDefaultTermTypeInstance(termTypes.get(product.getDefaultTermType().toLowerCase()));
				}
				
				instance.setDefaultCommTypeInstance(null);
				if (commTypes.containsKey(product.getDefaultCommissionCode().toLowerCase())) {
					instance.setDefaultCommTypeInstance(commTypes.get(product.getDefaultCommissionCode().toLowerCase()));
				}
				
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
