package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.scholastic.sbam.client.services.SnapshotTermDataListService;
import com.scholastic.sbam.server.database.codegen.CancelReason;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.Product;
import com.scholastic.sbam.server.database.codegen.Service;
import com.scholastic.sbam.server.database.codegen.SnapshotTermData;
import com.scholastic.sbam.server.database.codegen.TermType;
import com.scholastic.sbam.server.database.objects.DbCancelReason;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.objects.DbProduct;
import com.scholastic.sbam.server.database.objects.DbService;
import com.scholastic.sbam.server.database.objects.DbSnapshotTermData;
import com.scholastic.sbam.server.database.objects.DbTermType;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.CancelReasonInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.ProductInstance;
import com.scholastic.sbam.shared.objects.ServiceInstance;
import com.scholastic.sbam.shared.objects.SnapshotTermDataInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.objects.TermTypeInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class SnapshotTermDataListServiceImpl extends AuthenticatedServiceServlet implements SnapshotTermDataListService {

	@Override
	public SynchronizedPagingLoadResult<SnapshotTermDataInstance> getSnapshotTermData(PagingLoadConfig loadConfig, int snapshotId, long syncId) throws IllegalArgumentException, ServiceNotReadyException {
		authenticate("get snapshot term data", SecurityManager.ROLE_QUERY);

		//	These maps are used so that we'll reuse all instances, to be sure to conserve space in serialization
		
		HashMap<Integer, InstitutionInstance>	institutionMap = new HashMap<Integer, InstitutionInstance>();
		HashMap<String, ProductInstance>		productMap = new HashMap<String, ProductInstance>();
		HashMap<String, ServiceInstance>		serviceMap = new HashMap<String, ServiceInstance>();
		HashMap<String, CancelReasonInstance>	cancelReasonMap = new HashMap<String, CancelReasonInstance>();
		HashMap<String, TermTypeInstance> 		termTypeMap = new HashMap<String, TermTypeInstance>();
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<SnapshotTermDataInstance> list = new ArrayList<SnapshotTermDataInstance>();
		try {
			
			//	Find only undeleted cancel reasons
			List<SnapshotTermData> snapshotTermDatas = DbSnapshotTermData.findFiltered(snapshotId, -1, -1, null, null, -1, -1, null);

			for (SnapshotTermData snapshotTermData : snapshotTermDatas) {
				SnapshotTermDataInstance instance = DbSnapshotTermData.getInstance(snapshotTermData);

				instance.setInstitution(getInstitution(instance.getUcn(), institutionMap));
				
				instance.setProduct(getProduct(instance.getProductCode(), productMap));
				
				instance.setService(getService(instance.getServiceCode(), serviceMap));
				
				instance.setCancelReason(getCancelReason(instance.getCancelReasonCode(), cancelReasonMap));
				
				instance.setTermType(getTermType(instance.getTermTypeCode(), termTypeMap));
				
				list.add(instance);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return new SynchronizedPagingLoadResult<SnapshotTermDataInstance>(list, snapshotId, snapshotId, syncId);
	}
	
	protected ServiceInstance getService(String serviceCode, HashMap<String, ServiceInstance> serviceMap) {
		if (serviceCode == null || serviceCode.length() == 0)
			return null;
		
		if (serviceMap.containsKey(serviceCode))
			return serviceMap.get(serviceCode);
		
		Service service = DbService.getByCode(serviceCode);
		if (service != null) {
			serviceMap.put(serviceCode, DbService.getInstance(service));
			return serviceMap.get(serviceCode);
		}
		
		return null;
	}
	
	protected ProductInstance getProduct(String productCode, HashMap<String, ProductInstance> productMap) {
		if (productCode == null || productCode.length() == 0)
			return null;
		
		if (productMap.containsKey(productCode))
			return productMap.get(productCode);
		
		Product product = DbProduct.getByCode(productCode);
		if (product != null) {
			productMap.put(productCode, DbProduct.getInstance(product));
			return productMap.get(productCode);
		}
		
		return null;
	}
	
	protected CancelReasonInstance getCancelReason(String cancelReasonCode, HashMap<String, CancelReasonInstance> cancelReasonMap) {
		if (cancelReasonCode == null || cancelReasonCode.length() == 0)
			return null;
		
		if (cancelReasonMap.containsKey(cancelReasonCode))
			return cancelReasonMap.get(cancelReasonCode);
		
		CancelReason cancelReason = DbCancelReason.getByCode(cancelReasonCode);
		if (cancelReason != null) {
			cancelReasonMap.put(cancelReasonCode, DbCancelReason.getInstance(cancelReason));
			return cancelReasonMap.get(cancelReasonCode);
		}
		
		return null;
	}
	
	protected InstitutionInstance getInstitution(int ucn, HashMap<Integer, InstitutionInstance> institutionMap) {
		if (ucn <= 0)
			return null;
		
		if (institutionMap.containsKey(ucn))
			return institutionMap.get(ucn);
		
		Institution institution = DbInstitution.getByCode(ucn);
		if (institution != null) {
			institutionMap.put(ucn, DbInstitution.getInstance(institution));
			return institutionMap.get(ucn);
		}
		
		return null;
	}
	
	protected TermTypeInstance getTermType(String termTypeCode, HashMap<String, TermTypeInstance> termTypeMap) {
		if (termTypeCode == null || termTypeCode.length() == 0)
			return null;
		
		if (termTypeMap.containsKey(termTypeCode))
			return termTypeMap.get(termTypeCode);
		
		TermType termType = DbTermType.getByCode(termTypeCode);
		if (termType != null) {
			termTypeMap.put(termTypeCode, DbTermType.getInstance(termType));
			termTypeMap.get(termTypeCode);
		}
		
		return null;
	}
	
}
