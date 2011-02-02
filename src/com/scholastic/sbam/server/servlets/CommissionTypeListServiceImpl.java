package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.scholastic.sbam.client.services.CommissionTypeListService;
import com.scholastic.sbam.server.database.codegen.CommissionType;
import com.scholastic.sbam.server.database.objects.DbCommissionType;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class CommissionTypeListServiceImpl extends AuthenticatedServiceServlet implements CommissionTypeListService {

	@Override
	public List<CommissionTypeInstance> getCommissionTypes(LoadConfig loadConfig) throws IllegalArgumentException {
		
		authenticate("list commission types");	//	SecurityManager.ROLE_CONFIG);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<CommissionTypeInstance> list = new ArrayList<CommissionTypeInstance>();
		try {
			
			//	Find only undeleted commission types
			List<CommissionType> dbInstances = DbCommissionType.findFiltered(null, null, (char) 0, (char) 0, (char) 0, (char) 0, (char) 0, 'X');

			for (CommissionType dbInstance : dbInstances) {
				list.add(DbCommissionType.getInstance(dbInstance));
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return list;
	}
}
