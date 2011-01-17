package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.scholastic.sbam.client.services.CancelReasonListService;
import com.scholastic.sbam.server.database.codegen.CancelReason;
import com.scholastic.sbam.server.database.objects.DbCancelReason;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.CancelReasonInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class CancelReasonListServiceImpl extends AuthenticatedServiceServlet implements CancelReasonListService {

	@Override
	public List<CancelReasonInstance> getCancelReasons(LoadConfig loadConfig) throws IllegalArgumentException {
		
		authenticate("list cancel reasons", SecurityManager.ROLE_CONFIG);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<CancelReasonInstance> list = new ArrayList<CancelReasonInstance>();
		try {
			
			//	Find only undeleted cancel reasons
			List<CancelReason> cancelReasons = DbCancelReason.findFiltered(null, null, (char) 0, (char) 0, 'X');

			for (CancelReason cancelReason : cancelReasons) {
				CancelReasonInstance instance = new CancelReasonInstance();
				instance.setCancelReasonCode(cancelReason.getCancelReasonCode());
				instance.setDescription(cancelReason.getDescription());
				instance.setChangeNotCancel(cancelReason.getChangeNotCancel());
				instance.setStatus(cancelReason.getStatus());
				instance.setCreatedDatetime(cancelReason.getCreatedDatetime());
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
