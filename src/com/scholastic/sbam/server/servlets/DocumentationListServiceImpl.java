package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.scholastic.sbam.client.services.DocumentationListService;
import com.scholastic.sbam.server.database.codegen.Documentation;
import com.scholastic.sbam.server.database.objects.DbDocumentation;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.DocumentationInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class DocumentationListServiceImpl extends AuthenticatedServiceServlet implements DocumentationListService {

	@Override
	public List<DocumentationInstance> getDocumentationLinks(LoadConfig loadConfig) throws IllegalArgumentException {
		
		authenticate("list documentation links", SecurityManager.ROLE_ADMIN);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<DocumentationInstance> list = new ArrayList<DocumentationInstance>();
		try {
			
			//	Find only undeleted term types
			List<Documentation> dbInstances = DbDocumentation.findFiltered(null, (char) 0, AppConstants.STATUS_DELETED);

			for (Documentation dbInstance : dbInstances) {
				list.add(DbDocumentation.getInstance(dbInstance));
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return list;
	}
}
