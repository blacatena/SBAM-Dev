package com.scholastic.sbam.server.servlets;

import java.util.Date;
import java.util.List;

import com.scholastic.sbam.client.services.UpdateSnapshotService;
import com.scholastic.sbam.server.database.codegen.Snapshot;
import com.scholastic.sbam.server.database.objects.DbSnapshot;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.validation.AppSnapshotValidator;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.SnapshotInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateSnapshotServiceImpl extends AuthenticatedServiceServlet implements UpdateSnapshotService {

	@Override
	public UpdateResponse<SnapshotInstance> updateSnapshot(SnapshotInstance instance) throws IllegalArgumentException {
		
		boolean newCreated				= false;
		
		String	messages				= null;
		
		int referenceId	= instance.getSnapshotId();
		
		Snapshot dbInstance = null;
		
		Authentication auth = authenticate("update snapshot", SecurityManager.ROLE_QUERY);	// May later be used for logging activity
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Pre-edit/fix values
			validateInput(instance);
			
			//	Get existing, or create new
			if (instance.getSnapshotId() <= 0) {
				dbInstance = DbSnapshot.getById(instance.getSnapshotId());
			}

			//	If none found, create new
			if (dbInstance == null) {
				newCreated = true;
				dbInstance = new Snapshot();
				//	Set the create date/time
				dbInstance.setCreatedDatetime(new Date());
				dbInstance.setStatus('I');
			}

			//	Update values
//			if (instance.getSnapshotId() > 0)
//				dbInstance.setSnapshotId(instance.getSnapshotId());
			if (instance.getSnapshotName() != null)
				dbInstance.setSnapshotName(instance.getSnapshotName());
			if (instance.getSnapshotType() != null)
				dbInstance.setSnapshotType(instance.getSnapshotType());
			if (instance.getProductServiceType() != 0)
				dbInstance.setProductServiceType(instance.getProductServiceType());
			if (instance.getUcnType() != 0)
				dbInstance.setUcnType(instance.getUcnType());
			if (instance.getNote() != null)
				dbInstance.setNote(instance.getNote());
			if (instance.getOrgPath() != null)
				dbInstance.setOrgPath(instance.getOrgPath());
			if (instance.getSeq() >= 0)
				dbInstance.setSeq(instance.getSeq());
			//	Snapshot taken, rows and excelFilename are only updated by those processes.
//			if (instance.getSnapshotTaken() != null)
//				dbInstance.setSnapshotTaken(instance.getSnapshotTaken());
			if (instance.getStatus() != 0 && instance.getStatus() != dbInstance.getStatus())
				dbInstance.setStatus(instance.getStatus());
			
			dbInstance.setExpireDatetime(instance.getExpireDatetime());
			dbInstance.setCreateUserId(auth.getUserId());
			
			//	Fix nulls
			if (dbInstance.getSnapshotName() == null)
				dbInstance.setSnapshotName("");
			if (dbInstance.getSnapshotType() == null)
				dbInstance.setSnapshotType("");
			if (dbInstance.getProductServiceType() == 0)
				dbInstance.setProductServiceType(SnapshotInstance.NO_TERM_TYPE);
			if (dbInstance.getUcnType() == 0)
				dbInstance.setUcnType(SnapshotInstance.BILL_UCN_TYPE);
			if (dbInstance.getNote() == null)
				dbInstance.setNote("");
			if (dbInstance.getOrgPath() == null)
				dbInstance.setOrgPath("");
			if (dbInstance.getSeq() < 0)
				dbInstance.setSeq(0);
			if (dbInstance.getStatus() == 0)
				dbInstance.setStatus(AppConstants.STATUS_DELETED);
			
			//	Persist in database
			DbSnapshot.persist(dbInstance);
			
			//	Refresh when new row is created, to get assigned ID
			if (newCreated) {
			//	DbSnapshot.refresh(dbInstance);	// This may not be necessary, but just in case
				instance.setSnapshotId(dbInstance.getSnapshotId());
				instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
				instance.setCreateDisplayName(auth.getDisplayName());
			}
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		UpdateResponse<SnapshotInstance> response = new UpdateResponse<SnapshotInstance>(instance, messages);
		if (referenceId < 0) {
			response.setProperty("referenceId", referenceId);
		}
		return response;
	}
	
	private void validateInput(SnapshotInstance instance) throws IllegalArgumentException {
		AppSnapshotValidator validator = new AppSnapshotValidator();
		validator.setOriginal(instance);	//	This isn't really the original, but it's good enough, because it has the original ID
		testMessages(validator.validateSnapshot(instance));
	}
	
	private void testMessages(List<String> messages) throws IllegalArgumentException {
		if (messages != null)
			for (String message: messages)
				testMessage(message);
	}
	
	private void testMessage(String message) throws IllegalArgumentException {
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
