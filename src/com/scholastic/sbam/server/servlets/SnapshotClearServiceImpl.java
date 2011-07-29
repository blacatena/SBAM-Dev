package com.scholastic.sbam.server.servlets;

import java.util.Date;

import com.scholastic.sbam.client.services.SnapshotClearService;
import com.scholastic.sbam.server.database.codegen.Snapshot;
import com.scholastic.sbam.server.database.objects.DbSnapshot;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service to update just the name, status and/or note of a snapshot.
 */
@SuppressWarnings("serial")
public class SnapshotClearServiceImpl extends AuthenticatedServiceServlet implements SnapshotClearService {

	@Override
	public String clearSnapshot(int snapshotId, Date snapshotTaken) throws IllegalArgumentException {
		
		if (snapshotId <= 0)
			throw new IllegalArgumentException("Snapshot can only be cleared for an existing snapshot.");
		
		Snapshot dbInstance = null;
		
		authenticate("clear snapshot", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Get existing
			dbInstance = DbSnapshot.getById(snapshotId);
			if (dbInstance == null)
				throw new IllegalArgumentException("Snapshot " + snapshotId + " not found.");
			
			if (dbInstance.getSnapshotTaken() != null) {
				if (snapshotTaken == null || !snapshotTaken.equals(dbInstance.getSnapshotTaken())) {
					throw new IllegalArgumentException("INTERNAL SAFETY CHECK FAILED: Old snapshot taken date does not match that specified.");
				}
				dbInstance.setSnapshotTaken(null);
				dbInstance.setSnapshotRows(0);
				dbInstance.setExcelFilename(null);
				//	Persist in database
				DbSnapshot.persist(dbInstance);
			}
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The snapshot clear failed unexpectedly.");
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
