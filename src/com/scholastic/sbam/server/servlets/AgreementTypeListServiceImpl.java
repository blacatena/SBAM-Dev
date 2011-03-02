package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.scholastic.sbam.client.services.AgreementTypeListService;
import com.scholastic.sbam.server.database.codegen.AgreementType;
import com.scholastic.sbam.server.database.objects.DbAgreementType;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.AgreementTypeInstance;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AgreementTypeListServiceImpl extends AuthenticatedServiceServlet implements AgreementTypeListService {

	@Override
	public List<AgreementTypeInstance> getAgreementTypes(LoadConfig loadConfig) throws IllegalArgumentException {
		
		authenticate("list term types");	//	SecurityManager.ROLE_CONFIG);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<AgreementTypeInstance> list = new ArrayList<AgreementTypeInstance>();
		try {
			
			//	Find only undeleted term types
			List<AgreementType> dbInstances = DbAgreementType.findFiltered(null, null, null, (char) 0, (char) 0, 'X');

			for (AgreementType dbInstance : dbInstances) {
				list.add(DbAgreementType.getInstance(dbInstance));
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return list;
	}
}
