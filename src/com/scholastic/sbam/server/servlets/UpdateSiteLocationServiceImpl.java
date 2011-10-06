package com.scholastic.sbam.server.servlets;

import java.util.Date;
import java.util.List;

import com.scholastic.sbam.client.services.UpdateSiteLocationService;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.codegen.SiteId;
import com.scholastic.sbam.server.database.codegen.SitePreference;
import com.scholastic.sbam.server.database.codegen.SitePreferenceId;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.objects.DbPreferenceCategory;
import com.scholastic.sbam.server.database.objects.DbSite;
import com.scholastic.sbam.server.database.objects.DbSitePreference;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache.InstitutionCacheConflict;
import com.scholastic.sbam.server.fastSearch.SiteInstitutionCache;
import com.scholastic.sbam.server.validation.AppSiteValidator;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.PreferenceCategoryInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateSiteLocationServiceImpl extends AuthenticatedServiceServlet implements UpdateSiteLocationService {

	@Override
	public UpdateResponse<SiteInstance> updateSiteLocation(SiteInstance instance) throws IllegalArgumentException {
		
		authenticate("update site", SecurityManager.ROLE_MAINT);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			updateSite(instance);
			
			updateSitePreferences(instance);
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The site update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return new UpdateResponse<SiteInstance>(instance, null);
	}
	
	public static void updateSite(SiteInstance instance) throws InstitutionCacheConflict, Exception {
		
		boolean newCreated				= false;
		
		Site dbInstance = null;
		
//		Pre-edit/fix values
		validateInput(instance);
		
		//	Get existing, or create new
		if (instance.getUcn() > 0) {
			dbInstance = DbSite.getById(instance.getUcn(), instance.getUcnSuffix(), instance.getSiteLocCode());
		}

		//	If none found, create new
		if (dbInstance == null) {
			newCreated = true;
			dbInstance = new Site();
			SiteId id = new SiteId();
			id.setUcn(instance.getUcn());
			id.setUcnSuffix(instance.getUcnSuffix());
			id.setSiteLocCode(instance.getSiteLocCode());
			dbInstance.setId(id);
			//	Set the create date/time and status
			dbInstance.setCreatedDatetime(new Date());
		}

		//	Update values

		if (instance.getDescription() != null)
			dbInstance.setDescription(instance.getDescription());
		if (instance.getStatus() != 0)
			dbInstance.setStatus(instance.getStatus());
		if (instance.getCommissionCode() != null)
			dbInstance.setCommissionCode(instance.getCommissionCode());
		if (instance.getPseudoSite() != 0)
			dbInstance.setPseudoSite(instance.getPseudoSite());
		if (instance.getNote() != null)
			dbInstance.setNote(instance.getNote());
			
		//	Fix any nulls
		if (instance.getDescription() == null)
			dbInstance.setDescription("Missing Description");
		if (dbInstance.getStatus() == AppConstants.STATUS_ANY_NONE)
			dbInstance.setStatus(AppConstants.STATUS_ACTIVE);
		if (dbInstance.getCommissionCode() == null)
			dbInstance.setCommissionCode("");
		if (dbInstance.getPseudoSite() == 0)
			dbInstance.setPseudoSite('n');
		if (dbInstance.getNote() == null)
			dbInstance.setNote("");
		
		//	Persist in database
		DbSite.persist(dbInstance);
		
		//	Refresh when new row is created, to get assigned ID
		if (newCreated) {
		//	DbSite.refresh(dbInstance);	// This may not be necessary, but just in case
			instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
			DbSite.setDescriptions(instance);
		}
		
		//	Finally, record the site in the site cache
		if (SiteInstitutionCache.getSingleton() != null) {
			InstitutionInstance institution = instance.getInstitution();
			if (institution == null && instance.getUcn() > 0) {
				institution = DbInstitution.getInstance(DbInstitution.getByCode(instance.getUcn()));
			}
			if (institution != null)
				SiteInstitutionCache.getSingleton().addInstitution(institution);
		}
	}
	
	public void updateSitePreferences(SiteInstance site) {
		//	A null hash map means we're not updating preferences... it's a don't know/don't care situation
		if (site.getSelectedPreferences() == null)
			return;
		
		List<PreferenceCategoryInstance> categories = DbPreferenceCategory.findAllCatsAndCodes();	// We use the full list, so we can fill in the site values, otherwise we could use DbPreferenceCategory.findAll();
		for (PreferenceCategoryInstance category : categories) {
			SitePreference preference = DbSitePreference.getById(site.getUcn(), site.getUcnSuffix(), site.getSiteLocCode(), category.getPrefCatCode());
			if (site.getSelectedPreferences().containsKey(category.getPrefCatCode())) {
				String value = site.getSelectedPreferences().get(category.getPrefCatCode());
				if (value == null || value.trim().length() == 0) {
					if (preference != null)
						DbSitePreference.delete(preference);	// Could instead set status to DELETED
				} else {
					if (preference == null) {
						preference = new SitePreference();
						SitePreferenceId preferenceId = new SitePreferenceId();
						preferenceId.setUcn(site.getUcn());
						preferenceId.setUcnSuffix(site.getUcnSuffix());
						preferenceId.setSiteLocCode(site.getSiteLocCode());
						preferenceId.setPrefCatCode(category.getPrefCatCode());
						preference.setId(preferenceId);
					}
					preference.setPrefSelCode(value);
					preference.setStatus(AppConstants.STATUS_ACTIVE);
					preference.setCreatedDatetime(new Date());
					
					DbSitePreference.persist(preference);
				}
			} else {
				if (preference != null)
					DbSitePreference.delete(preference);		//	Could instead set status to DELETED
			}
		}
		
		//	Fill in the categories for the site
		site.setAllPreferenceCategories(categories);
	}
	
	protected static void validateInput(SiteInstance instance) throws IllegalArgumentException {
		AppSiteValidator validator = new AppSiteValidator();
		validator.setOriginal(instance);	//	This isn't really the original, but it's good enough, because it has the original ID
		testMessages(validator.validateSite(instance));
	}
	
	protected static void testMessages(List<String> messages) throws IllegalArgumentException {
		if (messages != null)
			for (String message: messages)
				testMessage(message);
	}
	
	protected static void testMessage(String message) throws IllegalArgumentException {
		if (message != null && message.length() > 0)
			throw new IllegalArgumentException(message);
	}
	
	private void silentRollback() {
		try {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.getSession().getTransaction().rollback();	
		} catch (Exception exc) { }
	}
}
