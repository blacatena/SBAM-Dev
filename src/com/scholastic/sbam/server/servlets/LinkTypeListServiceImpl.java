package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.scholastic.sbam.client.services.LinkTypeListService;
import com.scholastic.sbam.server.database.codegen.LinkType;
import com.scholastic.sbam.server.database.objects.DbLinkType;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.LinkTypeInstance;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class LinkTypeListServiceImpl extends AuthenticatedServiceServlet implements LinkTypeListService {

	@Override
	public List<LinkTypeInstance> getLinkTypes(LoadConfig loadConfig) throws IllegalArgumentException {
		
		authenticate("list link types");	//	SecurityManager.ROLE_CONFIG);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<LinkTypeInstance> list = new ArrayList<LinkTypeInstance>();
		try {
			
			//	Find only undeleted term types
			List<LinkType> dbInstances = DbLinkType.findFiltered(null, null, AppConstants.STATUS_ANY_NONE, AppConstants.STATUS_DELETED);

			for (LinkType dbInstance : dbInstances) {
				list.add(DbLinkType.getInstance(dbInstance));
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return list;
	}
}
