package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.scholastic.sbam.client.services.DeleteReasonListService;
import com.scholastic.sbam.server.database.codegen.DeleteReason;
import com.scholastic.sbam.server.database.objects.DbDeleteReason;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.DeleteReasonInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class DeleteReasonListServiceImpl extends AuthenticatedServiceServlet implements DeleteReasonListService {

	@Override
	public List<DeleteReasonInstance> getDeleteReasons(LoadConfig loadConfig) throws IllegalArgumentException {
		
		authenticate("list delete reasons", SecurityManager.ROLE_CONFIG);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<DeleteReasonInstance> list = new ArrayList<DeleteReasonInstance>();
		try {
//			String authUserName = null;
//			Authentication auth = ((Authentication) getServletContext().getAttribute(SecurityManager.AUTHENTICATION_ATTRIBUTE));
//			if (auth != null)
//				authUserName = auth.getUserName();
//			if (auth == null || authUserName == null || authUserName.length() == 0)
//				throw new Exception("No logged in user for whom to list users.");
//			if (!auth.hasRoleName(SecurityManager.ROLE_ADMIN))
//				throw new Exception("User is not privileged to list users.");
			
			//	Find only undeleted users
			List<DeleteReason> deleteReasons = DbDeleteReason.findFiltered(null, null, (char) 0, 'X');

			for (DeleteReason deleteReason : deleteReasons) {
				DeleteReasonInstance instance = new DeleteReasonInstance();
				instance.setDeleteReasonCode(deleteReason.getDeleteReasonCode());
				instance.setDescription(deleteReason.getDescription());
				instance.setStatus(deleteReason.getStatus());
				instance.setCreatedDatetime(deleteReason.getCreatedDatetime());
				list.add(instance);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return list;
	}
}
