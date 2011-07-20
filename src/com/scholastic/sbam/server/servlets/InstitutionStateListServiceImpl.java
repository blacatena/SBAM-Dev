package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.scholastic.sbam.client.services.InstitutionStateListService;
import com.scholastic.sbam.server.database.codegen.InstitutionState;
import com.scholastic.sbam.server.database.objects.DbInstitutionState;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.InstitutionStateInstance;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class InstitutionStateListServiceImpl extends AuthenticatedServiceServlet implements InstitutionStateListService {

	@Override
	public List<InstitutionStateInstance> getInstitutionStates(LoadConfig loadConfig) throws IllegalArgumentException {
		
		authenticate("list link types");	//	SecurityManager.ROLE_CONFIG);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<InstitutionStateInstance> list = new ArrayList<InstitutionStateInstance>();
		try {
			
			//	Find only undeleted term types
			List<InstitutionState> dbInstances = DbInstitutionState.findAll();

			for (InstitutionState dbInstance : dbInstances) {
				list.add(DbInstitutionState.getInstance(dbInstance));
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return list;
	}
}
