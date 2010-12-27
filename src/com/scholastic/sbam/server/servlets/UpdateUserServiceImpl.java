package com.scholastic.sbam.server.servlets;

import java.util.Date;

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
				throw new IllegalArgumentException("Requesting user is not authenticated.");
			
			//	Pre-edit/fix values
			if (instance.getUserName() == null)
				instance.setUserName("error");
			
		//	String updateUserName = auth.getUserName();
			User dbInstance = null;
			
			//	Get existing, or create new
			if (instance.getId() != null) {
				dbInstance = DbUser.getById(instance.getId());
			}

			//	If none found, create new
			if (dbInstance == null) {
				dbInstance = new User();
				dbInstance.setCreatedDatetime(new Date());
			}

			//	Update values
			if (instance.getUserName() != null)
				dbInstance.setUserName(instance.getUserName());
			if (instance.getPassword() != null)
				dbInstance.setPassword(instance.getPassword());
			if (instance.getFirstName() != null)
				dbInstance.setFirstName(instance.getFirstName());
			if (instance.getLastName() != null)
				dbInstance.setLastName(instance.getLastName());
			if (instance.getEmail() != null)
				dbInstance.setEmail(instance.getEmail());
			
			dbInstance.setStatus(instance.getStatus());
			
			//	Persist in database
			DbUser.persist(dbInstance);
			
			//	Refresh when new row is created, to get assigend ID
			if (dbInstance.getId() == null) {
				DbUser.refresh(dbInstance);
				instance.setId(dbInstance.getId());
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return instance;
	}
}
