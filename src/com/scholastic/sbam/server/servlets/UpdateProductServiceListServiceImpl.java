package com.scholastic.sbam.server.servlets;

import java.util.Date;
import java.util.List;

import com.scholastic.sbam.client.services.UpdateProductServiceListService;
import com.scholastic.sbam.server.database.codegen.Product;
import com.scholastic.sbam.server.database.codegen.ProductService;
import com.scholastic.sbam.server.database.codegen.ProductServiceId;
import com.scholastic.sbam.server.database.codegen.Service;
import com.scholastic.sbam.server.database.objects.DbProduct;
import com.scholastic.sbam.server.database.objects.DbProductService;
import com.scholastic.sbam.server.database.objects.DbService;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.objects.ProductServiceTreeInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateProductServiceListServiceImpl extends AuthenticatedServiceServlet implements UpdateProductServiceListService {
	
	private static final String ESCAPE_REG_EXP		= constructRegExp(AppConstants.PATH_ESCAPE);
	private static final String DELIMITER_REG_EXP	= constructRegExp(AppConstants.PATH_DELIMITER);
	private static final String ESCAPE_REPLACEMENT	= constructReplacement(AppConstants.PATH_ESCAPE);
	private static final String PATH_PREFIX			= "" + AppConstants.PATH_DELIMITER + AppConstants.PATH_ESCAPE;

	@Override
	public String updateProductServiceList(String productCode, List<ProductServiceTreeInstance> list) throws IllegalArgumentException {
		
		@SuppressWarnings("unused")
		Authentication auth = authenticate("update preference categories", SecurityManager.ROLE_CONFIG);	// May later be used for logging activity
		
		if (productCode == null || productCode.trim().length() == 0)
			throw new IllegalArgumentException("A product code is required.");
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			Product product = DbProduct.getByCode(productCode);
			if (product == null)
				throw new IllegalArgumentException("Product " + productCode + " does not exist in the database.");
			
			//	First, remove all services from this product
			List<ProductService> productServices = DbProductService.findByProduct(productCode, AppConstants.STATUS_DELETED);
			if (productServices != null) {
				for (ProductService productService : productServices) {
					if (productService.getStatus() != AppConstants.STATUS_DELETED) {
						productService.setStatus(AppConstants.STATUS_DELETED);
						DbProductService.persist(productService);
					}
				}
			}
			
			//	Cycle through the list, creating a folder tree, and updating services and product services as needed
			
			int seq = 0;
			
			for (ProductServiceTreeInstance instance : list) {
				instance.setProductCode(productCode);	//	In case it's wrong or missing, set the product code from the service call parameter
				seq = processTreeInstance(instance, seq, "" + AppConstants.PATH_DELIMITER + AppConstants.PATH_ESCAPE, productCode);
			}
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return "";
	}
	
	private int processTreeInstance(ProductServiceTreeInstance instance, int seq, String path, String productCode) {
		
		if (instance == null)
			return seq;
		
		seq++;
		
		if (instance.getType().equals(ProductServiceTreeInstance.FOLDER)) {
			//	Add folder name to the path, with the delimiter, after first escaping any instances of the escape character and delimiter character
			String fixedFolderName = instance.getDescription().replaceAll(ESCAPE_REG_EXP, ESCAPE_REPLACEMENT);
			fixedFolderName = fixedFolderName.replaceAll(DELIMITER_REG_EXP, "" + AppConstants.PATH_ESCAPE + AppConstants.PATH_DELIMITER);
			path += fixedFolderName + AppConstants.PATH_DELIMITER;
			//  Recursively process all children
			if (instance.getChildInstances() != null) {
				for (ProductServiceTreeInstance child : instance.getChildInstances()) {
					seq = processTreeInstance(child, seq, path, productCode);
				}
			}
		} else {
			if (instance.isSelected())
				updateProductService(instance);
			updateService(instance.getServiceCode(), seq, path);
		}
		
		//	Process all of the children
		if (instance.getChildInstances() != null) {
			for (ProductServiceTreeInstance child : instance.getChildInstances()) {
				if (child != null) {
					child.setProductCode(productCode);	//	In case it's wrong or missing, propagate the product code from the parent
					seq = processTreeInstance(child, seq, path, productCode);
				}
			}
		}
		
		return seq;
	}
	
	private void updateProductService(ProductServiceTreeInstance instance) {
		ProductService productService = DbProductService.getByCode(instance.getProductCode(), instance.getServiceCode());
		if (productService != null && productService.getStatus() != AppConstants.STATUS_ACTIVE) {
			productService.setStatus(AppConstants.STATUS_ACTIVE);
			DbProductService.persist(productService);
		} else {
			productService = new ProductService();
			ProductServiceId id = new ProductServiceId();
			id.setProductCode(instance.getProductCode());
			id.setServiceCode(instance.getServiceCode());
			productService.setId(id);
			productService.setCreatedDatetime(new Date());
			productService.setStatus(AppConstants.STATUS_ACTIVE);
		}
	}
	
	/**
	 * For a regular expression, precede any specially interpreted characters with a back slash.
	 * 
	 * @param value
	 * @return
	 */
	private static String constructRegExp(char value) {
		return "\\" + value;
//		if (value == '\\' || value == '.' || value == '*'|| value == '^' || value == '$')
//			return "\\" + value;
//		return value + "";
	}
	
	private static String constructReplacement(char value) {
		if (value == '\\')
			return "\\\\\\\\";
		return value + value + "";
	}
	
	private void updateService(String serviceCode, int seq, String path) {
		Service service = DbService.getByCode(serviceCode);
		
		if (seq == service.getSeq() && isUnchanged(path, service.getPresentationPath()))
			return;
		
		service.setSeq(seq);
		if (path != null && !path.equals(PATH_PREFIX))
			service.setPresentationPath(path);
		else
			service.setPresentationPath("");
		
		DbService.persist(service);
	}
	
	private boolean isUnchanged(String first, String second) {
		if (first == null && second == null)
			return false;
		if (first == null || second == null)
			return true;
		return first.equals(second);
	}
	
	protected void testMessages(List<String> messages) throws IllegalArgumentException {
		if (messages != null)
			for (String message: messages)
				testMessage(message);
	}
	

	protected void testMessage(String message) throws IllegalArgumentException {
		if (message != null && message.length() > 0)
			throw new IllegalArgumentException(message);
	}
	

	protected void silentRollback() {
		try {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.getSession().getTransaction().rollback();	
		} catch (Exception exc) { }
	}
}
