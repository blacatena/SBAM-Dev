package com.scholastic.sbam.server.servlets;

import java.util.Date;

import com.scholastic.sbam.client.services.UpdateSnapshotBasicsService;
import com.scholastic.sbam.server.database.codegen.Snapshot;
import com.scholastic.sbam.server.database.objects.DbSnapshot;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.validation.AppSnapshotValidator;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.SnapshotInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service to update just the name, status and/or note of a snapshot.
 */
@SuppressWarnings("serial")
public class UpdateSnapshotBasicsServiceImpl extends AuthenticatedServiceServlet implements UpdateSnapshotBasicsService {

	@Override
	public UpdateResponse<SnapshotInstance> updateSnapshotBasics(int snapshotId, String snapshotName, char snapshotStatus, Date expireDatetime, String note) throws IllegalArgumentException {
		
		if (snapshotId <= 0)
			throw new IllegalArgumentException("Basic info can only be updated for an existing snapshot.");
		
		Snapshot dbInstance = null;
		
		authenticate("update snapshot basics", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Get existing
			dbInstance = DbSnapshot.getById(snapshotId);
			if (dbInstance == null)
				throw new IllegalArgumentException("Snapshot " + snapshotId + " not found.");
				

			//	Update values
			
			if (snapshotName != null) {
				AppSnapshotValidator validator = new AppSnapshotValidator();
				validator.validateSnapshotName(snapshotName);
				if (validator.getMessages().size() > 0)
					throw new IllegalArgumentException(validator.getMessages().get(0));
				dbInstance.setSnapshotName(snapshotName);
			}
			
			if (snapshotStatus > AppConstants.STATUS_ANY_NONE) {
				dbInstance.setStatus(snapshotStatus);
			}
			
			dbInstance.setExpireDatetime(expireDatetime);
			
			if (note != null) {
				if (note.equalsIgnoreCase("<br>"))
					note = "";
				dbInstance.setNote(note);
			}

			
			//	Persist in database
			DbSnapshot.persist(dbInstance);
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The snapshot basics update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return new UpdateResponse<SnapshotInstance>(DbSnapshot.getInstance(dbInstance));
	}
	
	private void silentRollback() {
		try {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.getSession().getTransaction().rollback();	
		} catch (Exception exc) { }
	}
}
