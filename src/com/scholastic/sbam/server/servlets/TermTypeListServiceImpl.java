package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.scholastic.sbam.client.services.TermTypeListService;
import com.scholastic.sbam.server.database.codegen.TermType;
import com.scholastic.sbam.server.database.objects.DbTermType;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.TermTypeInstance;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class TermTypeListServiceImpl extends AuthenticatedServiceServlet implements TermTypeListService {

	@Override
	public List<TermTypeInstance> getTermTypes(LoadConfig loadConfig) throws IllegalArgumentException {
		
		authenticate("list term types");	//	SecurityManager.ROLE_CONFIG);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<TermTypeInstance> list = new ArrayList<TermTypeInstance>();
		try {
			
			//	Find only undeleted term types
			List<TermType> dbInstances = DbTermType.findFiltered(null, null, (char) 0, (char) 0, 'X');

			for (TermType dbInstance : dbInstances) {
				list.add(DbTermType.getInstance(dbInstance));
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return list;
	}
}
