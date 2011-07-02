package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.UpdateSnapshotNoteService;
import com.scholastic.sbam.server.database.codegen.Snapshot;
import com.scholastic.sbam.server.database.objects.DbSnapshot;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.SnapshotInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateSnapshotNoteServiceImpl extends AuthenticatedServiceServlet implements UpdateSnapshotNoteService {

	@Override
	public UpdateResponse<SnapshotInstance> updateSnapshotNote(SnapshotInstance instance) throws IllegalArgumentException {
		
		if (instance.isNewRecord())
			throw new IllegalArgumentException("Notes can only be updated for an existing snapshot.");
		
		Snapshot dbInstance = null;
		
		authenticate("update snapshot note", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Get existing
			dbInstance = DbSnapshot.getByCode(instance.getSnapshotCode());
			if (dbInstance == null)
				throw new IllegalArgumentException("Snapshot " + instance + " not found.");
				

			//	Update values
			
			if (instance.getNote() != null) {
				if (instance.getNote().equalsIgnoreCase("<br>"))
					instance.setNote("");
				dbInstance.setNote(instance.getNote());
			}
			
			//	Persist in database
			DbSnapshot.persist(dbInstance);
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The snapshot note update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return new UpdateResponse<SnapshotInstance>(instance);
	}
	
	private void silentRollback() {
		try {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.getSession().getTransaction().rollback();	
		} catch (Exception exc) { }
	}
}
