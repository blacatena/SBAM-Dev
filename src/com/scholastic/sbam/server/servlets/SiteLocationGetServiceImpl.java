package com.scholastic.sbam.server.servlets;

import java.util.HashMap;
import java.util.List;

import com.scholastic.sbam.client.services.SiteLocationGetService;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.codegen.SitePreference;
import com.scholastic.sbam.server.database.objects.DbPreferenceCategory;
import com.scholastic.sbam.server.database.objects.DbSite;
import com.scholastic.sbam.server.database.objects.DbSitePreference;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class SiteLocationGetServiceImpl extends AuthenticatedServiceServlet implements SiteLocationGetService {

	@Override
	public SiteInstance getSiteLocation(int ucn, int ucnSuffix, String siteLocCode, boolean includePreferences, boolean includePreferenceList) throws IllegalArgumentException {
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
				DbSite.setDescriptions(result);
				
				if (includePreferences) {
					result.setSelectedPreferences(new HashMap<String, String>());
					List<SitePreference> preferences = DbSitePreference.findBySite(ucn, ucnSuffix, siteLocCode, AppConstants.STATUS_ACTIVE, AppConstants.STATUS_DELETED);
					for (SitePreference preference : preferences)
						result.getSelectedPreferences().put(preference.getId().getPrefCatCode(), preference.getPrefSelCode());
				}
				
				if (includePreferenceList) {
					result.setAllPreferenceCategories(DbPreferenceCategory.findAllCatsAndCodes());
				}
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
