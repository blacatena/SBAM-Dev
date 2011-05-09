package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.SiteLocationGetService;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.objects.DbSite;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class SiteLocationGetServiceImpl extends AuthenticatedServiceServlet implements SiteLocationGetService {

	@Override
	public SiteInstance getSiteLocation(int ucn, int ucnSuffix, String siteLocCode) throws IllegalArgumentException {
		authenticate("get institution", SecurityManager.ROLE_QUERY);

		if (ucn <= 0)
			throw new IllegalArgumentException("A UCN is required.");
		if (ucnSuffix <= 0)
			throw new IllegalArgumentException("A UCN suffix is required.");
		if (siteLocCode == null)
			throw new IllegalArgumentException("A Site Location Code is required.");
		
		SiteInstance result = null;
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		try {
			Site dbInstance = DbSite.getById(ucn, ucnSuffix, siteLocCode);
			
			if (dbInstance != null) {
				result = DbSite.getInstance(dbInstance);
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
