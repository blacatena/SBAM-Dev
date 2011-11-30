package com.scholastic.sbam.server.servlets;

import java.util.Date;
import java.util.List;

import com.scholastic.sbam.client.services.UpdateStatsAdminService;
import com.scholastic.sbam.server.database.codegen.StatsAdmin;
import com.scholastic.sbam.server.database.objects.DbStatsAdmin;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache.InstitutionCacheConflict;
import com.scholastic.sbam.server.validation.AppStatsAdminValidator;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.StatsAdminInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateStatsAdminServiceImpl extends AuthenticatedServiceServlet implements UpdateStatsAdminService {

	@Override
	public UpdateResponse<StatsAdminInstance> updateStatsAdmin(StatsAdminInstance instance) throws IllegalArgumentException {
		
		authenticate("update stats admin", SecurityManager.ROLE_MAINT);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			updateStatsAdminInstance(instance);
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The stats admin update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return new UpdateResponse<StatsAdminInstance>(instance, null);
	}
	
	public static void updateStatsAdminInstance(StatsAdminInstance instance) throws InstitutionCacheConflict, Exception {
		
		boolean newCreated				= false;
		
		StatsAdmin dbInstance = null;
		
//		Pre-edit/fix values
		validateInput(instance);
		
		//	Get existing, or create new
		if (instance.getUcn() > 0) {
			dbInstance = DbStatsAdmin.getById(instance.getUcn());
		}

		//	If none found, create new
		if (dbInstance == null) {
			newCreated = true;
			dbInstance = new StatsAdmin();
			dbInstance.setUcn(instance.getUcn());
			//	Set the create date/time and status
			dbInstance.setCreatedDatetime(new Date());
		}

		//	Update values

		if (instance.getStatus() != 0)
			dbInstance.setStatus(instance.getStatus());
		if (instance.getAdminUid() != null)
			dbInstance.setAdminUid(instance.getAdminUid());
		if (instance.getAdminPassword() != null)
			dbInstance.setAdminPassword(instance.getAdminPassword());
		if (instance.getStatsGroup() != null)
			dbInstance.setStatsGroup(instance.getStatsGroup());
		if (instance.getNote() != null)
			dbInstance.setNote(instance.getNote());
			
		//	Fix any nulls
		if (dbInstance.getStatus() == AppConstants.STATUS_ANY_NONE)
			dbInstance.setStatus(AppConstants.STATUS_ACTIVE);
		if (dbInstance.getAdminUid() == null)
			dbInstance.setAdminUid("");
		if (dbInstance.getAdminPassword() == null)
			dbInstance.setAdminPassword("");
		if (dbInstance.getStatsGroup() == null)
			dbInstance.setStatsGroup("");
		if (dbInstance.getNote() == null)
			dbInstance.setNote("");
		
		//	Persist in database
		DbStatsAdmin.persist(dbInstance);
		
		//	Refresh when new row is created, to get assigned ID
		if (newCreated) {
		//	DbStatsAdmin.refresh(dbInstance);	// This may not be necessary, but just in case
			instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
//			DbStatsAdmin.setDescriptions(instance, instance.getInstitution());
		}
		
		//	Finally, record the site in the site cache
//		if (SiteInstitutionCache.getSingleton() != null) {
//			InstitutionInstance institution = instance.getInstitution();
//			if (institution == null && instance.getUcn() > 0) {
//				institution = DbInstitution.getInstance(DbInstitution.getByCode(instance.getUcn()));
//			}
//			if (institution != null)
//				SiteInstitutionCache.getSingleton().addInstitution(institution);
//		}
	}
	
	protected static void validateInput(StatsAdminInstance instance) throws IllegalArgumentException {
		AppStatsAdminValidator validator = new AppStatsAdminValidator();
		validator.setOriginal(instance);	//	This isn't really the original, but it's good enough, because it has the original ID
		testMessages(validator.validateStatsAdmin(instance));
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
