package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.scholastic.sbam.client.services.ServiceListService;
import com.scholastic.sbam.server.database.codegen.Service;
import com.scholastic.sbam.server.database.objects.DbService;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.ServiceInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ServiceListServiceImpl extends AuthenticatedServiceServlet implements ServiceListService {

	@Override
	public List<ServiceInstance> getServices(LoadConfig loadConfig) throws IllegalArgumentException {
		
		authenticate("list cancel reasons", SecurityManager.ROLE_CONFIG);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<ServiceInstance> list = new ArrayList<ServiceInstance>();
		try {
			
			//	Find only undeleted cancel reasons
			List<Service> services = DbService.findFiltered(null, null, (char) 0, null, (char) 0, 'X');

			for (Service service : services) {
				ServiceInstance instance = new ServiceInstance();
				instance.setServiceCode(service.getServiceCode());
				instance.setDescription(service.getDescription());
				instance.setServiceType(service.getServiceType());
				instance.setExportValue(service.getExportValue());
				instance.setExportFile(service.getExportFile());
				instance.setStatus(service.getStatus());
				instance.setCreatedDatetime(service.getCreatedDatetime());
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
