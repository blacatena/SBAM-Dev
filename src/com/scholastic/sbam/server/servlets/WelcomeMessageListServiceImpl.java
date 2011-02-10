package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.scholastic.sbam.client.services.WelcomeMessageListService;
import com.scholastic.sbam.server.database.codegen.WelcomeMessage;
import com.scholastic.sbam.server.database.objects.DbWelcomeMessage;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.WelcomeMessageInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class WelcomeMessageListServiceImpl extends AuthenticatedServiceServlet implements WelcomeMessageListService {

	@Override
	public List<WelcomeMessageInstance> getWelcomeMessageList(LoadConfig loadConfig) throws IllegalArgumentException {
		
		authenticate("list welcome messages", SecurityManager.ROLE_ADMIN);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<WelcomeMessageInstance> list = new ArrayList<WelcomeMessageInstance>();
		try {
			
			//	Find only undeleted term types
			Date expireDateFilter = null;
			if (loadConfig.get("expireDate") != null && loadConfig.get("expireDate") instanceof Date)
				expireDateFilter = (Date) loadConfig.get("expireDate");
				
			List<WelcomeMessage> dbInstances = DbWelcomeMessage.findAfterExpireDate(expireDateFilter);

			for (WelcomeMessage dbInstance : dbInstances) {
				list.add(DbWelcomeMessage.getInstance(dbInstance));
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return list;
	}
}
