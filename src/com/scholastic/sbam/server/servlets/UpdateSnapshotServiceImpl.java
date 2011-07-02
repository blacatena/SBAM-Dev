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
		
		Snapshot dbInstance = null;
		
		@SuppressWarnings("unused")
		Authentication auth = authenticate("update snapshot", SecurityManager.ROLE_CONFIG);	// May later be used for logging activity
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Pre-edit/fix values
			validateInput(instance);
			
			//	Get existing, or create new
			if (instance.getSnapshotCode() != null) {
				dbInstance = DbSnapshot.getByCode(instance.getSnapshotCode());
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
			if (instance.getSnapshotCode() != null)
				dbInstance.setSnapshotCode(instance.getSnapshotCode());
			if (instance.getSnapshotName() != null)
				dbInstance.setSnapshotName(instance.getSnapshotName());
			if (instance.getSnapshotType() != null)
				dbInstance.setSnapshotType(instance.getSnapshotType());
			if (instance.getProductServiceType() != 0)
				dbInstance.setProductServiceType(instance.getProductServiceType());
			if (instance.getNote() != null)
				dbInstance.setNote(instance.getNote());
			if (instance.getOrgPath() != null)
				dbInstance.setOrgPath(instance.getOrgPath());
			if (instance.getSeq() >= 0)
				dbInstance.setSeq(instance.getSeq());
			if (instance.getSnapshotTaken() != null)
				dbInstance.setSnapshotTaken(instance.getSnapshotTaken());
			if (instance.getStatus() != 0 && instance.getStatus() != dbInstance.getStatus())
				dbInstance.setStatus(instance.getStatus());
			
			//	Fix nulls
			if (dbInstance.getSnapshotName() == null)
				dbInstance.setSnapshotName("");
			if (dbInstance.getSnapshotType() == null)
				dbInstance.setSnapshotType("");
			if (dbInstance.getProductServiceType() == 0)
				dbInstance.setProductServiceType(SnapshotInstance.NO_TERM_TYPE);
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
			//	instance.setId(dbInstance.getId());	// Not auto-increment, so not needed
				instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
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
		
		return new UpdateResponse<SnapshotInstance>(instance, messages);
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
