package com.scholastic.sbam.server.servlets;

import java.util.List;

import com.scholastic.sbam.client.services.UpdatePreferenceCodeSeqService;
import com.scholastic.sbam.server.database.codegen.PreferenceCode;
import com.scholastic.sbam.server.database.objects.DbPreferenceCode;
import com.scholastic.sbam.server.database.util.HibernateUtil;import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdatePreferenceCodeSeqServiceImpl extends AuthenticatedServiceServlet implements UpdatePreferenceCodeSeqService {

	@Override
	public String updatePreferenceCodeSeq(String catCode, List<String> sequence) throws IllegalArgumentException {
		
		PreferenceCode dbInstance = null;
		
		@SuppressWarnings("unused")
		Authentication auth = authenticate("update preference categories", SecurityManager.ROLE_CONFIG);	// May later be used for logging activity
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			int i = 0;
			for (String code : sequence) {
				dbInstance = DbPreferenceCode.getByCode(catCode, code);
				if (dbInstance == null)
					throw new IllegalArgumentException("Invalid preference code " + code + " for category " + catCode + " in the sequence.");
				dbInstance.setSeq(i++);
				DbPreferenceCode.persist(dbInstance);
			}
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The preference code resequence failed unexpectedly.");
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
