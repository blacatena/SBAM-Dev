package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.scholastic.sbam.client.services.TermTypeListService;
import com.scholastic.sbam.server.database.codegen.TermType;
import com.scholastic.sbam.server.database.objects.DbTermType;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.TermTypeInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class TermTypeListServiceImpl extends AuthenticatedServiceServlet implements TermTypeListService {

	@Override
	public List<TermTypeInstance> getTermTypes(LoadConfig loadConfig) throws IllegalArgumentException {
		
		authenticate("list cancel reasons", SecurityManager.ROLE_CONFIG);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<TermTypeInstance> list = new ArrayList<TermTypeInstance>();
		try {
			
			//	Find only undeleted cancel reasons
			List<TermType> cancelReasons = DbTermType.findFiltered(null, null, (char) 0, (char) 0, 'X');

			for (TermType cancelReason : cancelReasons) {
				TermTypeInstance instance = new TermTypeInstance();
				instance.setTermTypeCode(cancelReason.getTermTypeCode());
				instance.setDescription(cancelReason.getDescription());
				instance.setActivate(cancelReason.getActivate());
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
