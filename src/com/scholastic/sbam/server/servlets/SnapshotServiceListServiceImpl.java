package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.scholastic.sbam.client.services.SnapshotServiceListService;
import com.scholastic.sbam.server.database.codegen.SnapshotProductService;
import com.scholastic.sbam.server.database.codegen.Service;
import com.scholastic.sbam.server.database.objects.DbSnapshotProductService;
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
public class SnapshotServiceListServiceImpl extends ServiceTreeServiceBase implements SnapshotServiceListService {
	
	@Override
	public List<ProductServiceTreeInstance> getSnapshotServices(String snapshotCode, LoadConfig loadConfig) throws IllegalArgumentException {
		
		authenticate("list snapshot services", SecurityManager.ROLE_QUERY);
		
		if (snapshotCode == null || snapshotCode.length() == 0)
			throw new IllegalArgumentException("Snapshot code is a required argument.");
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		ProductServiceTreeInstance root = null;

		List<ProductServiceTreeInstance> list = new ArrayList<ProductServiceTreeInstance>();
		try {
			
			//	Find only undeleted product services
			List<SnapshotProductService> productServices = DbSnapshotProductService.findServiceBySnapshot(snapshotCode, 'X');
			
			//	Create a hash set of the selected product services
			HashSet<String> selectedServices = new HashSet<String>();
			for (SnapshotProductService productService : productServices) {
				selectedServices.add(productService.getId().getProductServiceCode());
			}
			
			List<Service> services = DbService.findUndeleted();

			for (Service service : services) {
				ProductServiceTreeInstance instance = new ProductServiceTreeInstance();
				
				instance.setProductCode(null);
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
}
