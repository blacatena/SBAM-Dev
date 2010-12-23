package com.scholastic.sbam.server.servlets;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.scholastic.sbam.client.services.UpdateUserService;
import com.scholastic.sbam.server.database.codegen.User;
import com.scholastic.sbam.server.database.objects.DbUser;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.objects.UserInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateUserServiceImpl extends RemoteServiceServlet implements UpdateUserService {

	@Override
	public UserInstance updateUser(UserInstance instance) throws IllegalArgumentException {
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			Authentication auth = (Authentication) getServletContext().getAttribute(SecurityManager.AUTHENTICATION_ATTRIBUTE);
			//	If user is no longer logged in, just skip this update
			if (auth == null)
				return null;
			
			System.out.println(instance.getId());
			System.out.println(instance.getUserName());
			System.out.println(instance.getFirstName());
			System.out.println(instance.getLastName());
			System.out.println(instance.getEmail());
			System.out.println(instance.getStatus());
			
		//	String updateUserName = auth.getUserName();
			User dbInstance = null;
			
			if (instance.getId() != null) {
				dbInstance = DbUser.getById(instance.getId());
			} else {
				dbInstance = new User();
				dbInstance.setStatus('A');
				dbInstance.setUserName(instance.getUserName());
			}

		//	DbUserMessage.persist(dbInstance);
		//	if (dbInstance.getId() == null)
		//		DbUserMessage.refresh(dbInstance);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return instance;
	}
}
