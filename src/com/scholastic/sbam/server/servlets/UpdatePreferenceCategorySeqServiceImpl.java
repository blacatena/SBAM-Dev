package com.scholastic.sbam.server.servlets;

import java.util.List;

import com.scholastic.sbam.client.services.UpdatePreferenceCategorySeqService;
import com.scholastic.sbam.server.database.codegen.PreferenceCategory;
import com.scholastic.sbam.server.database.objects.DbPreferenceCategory;
import com.scholastic.sbam.server.database.util.HibernateUtil;import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdatePreferenceCategorySeqServiceImpl extends AuthenticatedServiceServlet implements UpdatePreferenceCategorySeqService {

	@Override
	public String updatePreferenceCategorySeq(List<String> sequence) throws IllegalArgumentException {
		
		PreferenceCategory dbInstance = null;
		
		@SuppressWarnings("unused")
		Authentication auth = authenticate("update preference categories", SecurityManager.ROLE_CONFIG);	// May later be used for logging activity
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			int i = 0;
			for (String catCode : sequence) {
				dbInstance = DbPreferenceCategory.getByCode(catCode);
				if (dbInstance == null)
					throw new IllegalArgumentException("Invalid category code " + catCode + " in the sequence.");
				dbInstance.setSeq(i++);
				DbPreferenceCategory.persist(dbInstance);
			}
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The preference category resequence failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return "";
	}
	
	private void silentRollback() {
		try {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.getSession().getTransaction().rollback();	
		} catch (Exception exc) { }
	}
}
