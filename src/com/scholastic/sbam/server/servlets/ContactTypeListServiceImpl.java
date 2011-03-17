package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.scholastic.sbam.client.services.ContactTypeListService;
import com.scholastic.sbam.server.database.codegen.ContactType;
import com.scholastic.sbam.server.database.objects.DbContactType;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.ContactTypeInstance;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ContactTypeListServiceImpl extends AuthenticatedServiceServlet implements ContactTypeListService {

	@Override
	public List<ContactTypeInstance> getContactTypes(LoadConfig loadConfig) throws IllegalArgumentException {
		
		authenticate("list contact types");	//	SecurityManager.ROLE_CONFIG);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<ContactTypeInstance> list = new ArrayList<ContactTypeInstance>();
		try {
			
			//	Find only undeleted term types
			List<ContactType> dbInstances = DbContactType.findFiltered(null, null, AppConstants.STATUS_ANY_NONE, AppConstants.STATUS_DELETED);

			for (ContactType dbInstance : dbInstances) {
				list.add(DbContactType.getInstance(dbInstance));
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return list;
	}
}
