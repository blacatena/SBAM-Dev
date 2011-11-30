package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.StatsAdminGetService;
import com.scholastic.sbam.server.database.codegen.StatsAdmin;
import com.scholastic.sbam.server.database.objects.DbStatsAdmin;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.StatsAdminInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class StatsAdminGetServiceImpl extends AuthenticatedServiceServlet implements StatsAdminGetService {

	@Override
	public StatsAdminInstance getStatsAdmin(int ucn) throws IllegalArgumentException {
		authenticate("get institution", SecurityManager.ROLE_QUERY);

		if (ucn <= 0)
			throw new IllegalArgumentException("A UCN is required.");
		
		StatsAdminInstance result = null;
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		try {
			StatsAdmin dbInstance = DbStatsAdmin.getById(ucn);
			
			if (dbInstance != null) {
				result = DbStatsAdmin.getInstance(dbInstance);
//				DbStatsAdmin.setDescriptions(result, null);
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
		} finally {		
			HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return result;
	}
}
