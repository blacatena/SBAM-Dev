package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.DuplicateSnapshotService;
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
public class DuplicateSnapshotServiceImpl extends AuthenticatedServiceServlet implements DuplicateSnapshotService {

	@Override
	public UpdateResponse<SnapshotInstance> duplicateSnapshot(int snapshotId, String snapshotName) throws IllegalArgumentException {
		
		if (snapshotId <= 0)
			throw new IllegalArgumentException("Only existing snapshot can be duplicated.");
		
		Snapshot dbInstance = null;
		Snapshot dbOriginal = null;
		
		authenticate("duplicate snapshot", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Get existing
			dbOriginal = DbSnapshot.getById(snapshotId);
			if (dbOriginal == null)
				throw new IllegalArgumentException("Snapshot " + snapshotId + " not found.");
				
			dbInstance = new Snapshot();

			//	Update values
			
			if (snapshotName != null) {
				AppSnapshotValidator validator = new AppSnapshotValidator();
				validator.validateSnapshotName(snapshotName);
				if (validator.getMessages().size() > 0)
					throw new IllegalArgumentException(validator.getMessages().get(0));
				dbInstance.setSnapshotName(snapshotName);
			} else
				dbInstance.setSnapshotName(dbOriginal.getSnapshotName() + " Copy");
			
			dbInstance.setStatus(AppConstants.STATUS_ACTIVE);
			
			dbInstance.setSnapshotType(dbOriginal.getSnapshotType());
			dbInstance.setSnapshotTaken(null);
			dbInstance.setOrgPath(dbOriginal.getOrgPath());
			dbInstance.setProductServiceType(dbOriginal.getProductServiceType());
			dbInstance.setSeq(dbOriginal.getSeq());
			
			String note = dbOriginal.getNote();
			if (note.length() > 0)
				note += " <i>Copied from original snapshot " + snapshotId + " : " + dbOriginal.getSnapshotName() + ".</i>";
			dbInstance.setNote(note);

			//	Persist in database
			DbSnapshot.persist(dbInstance);

			copySubordinateTables(dbInstance, dbOriginal);
			
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
	
	public void copySubordinateTables(Snapshot dbInstance, Snapshot dbOriginal) {
		
	}
}
