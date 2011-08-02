package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.scholastic.sbam.client.services.SnapshotProductListService;
import com.scholastic.sbam.server.database.codegen.SnapshotProductService;
import com.scholastic.sbam.server.database.codegen.Product;
import com.scholastic.sbam.server.database.objects.DbSnapshotProductService;
import com.scholastic.sbam.server.database.objects.DbProduct;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.SnapshotProductTreeInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC product to list product product assignments.
 * 
 * Note that this class can be generalized for other implementations by making SnapshotProductTreeInstance an implementation that requires the
 * getters and setters for Description, Type, and Children.
 */
@SuppressWarnings("serial")
public class SnapshotProductListServiceImpl extends TreeListServiceBase<SnapshotProductTreeInstance> implements SnapshotProductListService {
	
	@Override
	public List<SnapshotProductTreeInstance> getSnapshotProducts(int snapshotId, LoadConfig loadConfig) throws IllegalArgumentException {
		
		authenticate("list snapshot products", SecurityManager.ROLE_QUERY);
		
		if (snapshotId <= 0)
			throw new IllegalArgumentException("Snapshot ID is a required argument.");
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		SnapshotProductTreeInstance root = null;

		List<SnapshotProductTreeInstance> list = new ArrayList<SnapshotProductTreeInstance>();
		try {
			
			//	Find only undeleted product products
			List<SnapshotProductService> productServices = DbSnapshotProductService.findProductBySnapshot(snapshotId, AppConstants.STATUS_DELETED);
			
			//	Create a hash set of the selected product products
			HashSet<String> selectedProducts = new HashSet<String>();
			for (SnapshotProductService productService : productServices) {
				selectedProducts.add(productService.getId().getProductServiceCode());
			}
			
			List<Product> products = DbProduct.findFiltered(null, null, null, null, AppConstants.STATUS_ACTIVE, AppConstants.STATUS_DELETED);

			for (Product product : products) {
				SnapshotProductTreeInstance instance = new SnapshotProductTreeInstance();
				
				instance.setSnapshotId(snapshotId);
				instance.setProductCode(product.getProductCode());
				instance.setDescription(product.getDescription());
				instance.setType(SnapshotProductTreeInstance.SERVICE);
				instance.setSelected(selectedProducts.contains(product.getProductCode()));
				
				root = addFolderTree(list, root, instance, product.getOrgPath());
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return list;
	}

	@Override
	protected SnapshotProductTreeInstance getTreeInstance() {
		return new SnapshotProductTreeInstance();
	}
}
