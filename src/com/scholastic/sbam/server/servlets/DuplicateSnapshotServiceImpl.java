package com.scholastic.sbam.server.servlets;

import java.util.Date;
import java.util.List;

import com.scholastic.sbam.client.services.DuplicateSnapshotService;
import com.scholastic.sbam.server.database.codegen.Snapshot;
import com.scholastic.sbam.server.database.codegen.SnapshotParameter;
import com.scholastic.sbam.server.database.codegen.SnapshotParameterId;
import com.scholastic.sbam.server.database.codegen.SnapshotProductService;
import com.scholastic.sbam.server.database.codegen.SnapshotProductServiceId;
import com.scholastic.sbam.server.database.objects.DbSnapshot;
import com.scholastic.sbam.server.database.objects.DbSnapshotParameter;
import com.scholastic.sbam.server.database.objects.DbSnapshotProductService;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.validation.AppSnapshotValidator;
import com.scholastic.sbam.shared.objects.Authentication;
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
		
		Authentication auth = authenticate("duplicate snapshot", SecurityManager.ROLE_QUERY);
		
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
			dbInstance.setSnapshotRows(0);
			dbInstance.setExcelFilename(null);
			dbInstance.setOrgPath(dbOriginal.getOrgPath());
			dbInstance.setUcnType(dbOriginal.getUcnType());
			dbInstance.setProductServiceType(dbOriginal.getProductServiceType());
			dbInstance.setSeq(dbOriginal.getSeq());
			dbInstance.setExpireDatetime(dbOriginal.getExpireDatetime());
			dbInstance.setCreateUserId(auth.getUserId());
			dbInstance.setCreatedDatetime(new Date());
			
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
		copySnapshotParameters(dbInstance, dbOriginal);
		copySnapshotProductServices(dbInstance, dbOriginal);
	}
	
	public void copySnapshotParameters(Snapshot dbInstance, Snapshot dbOriginal) {
		List<SnapshotParameter> parameterOriginals = DbSnapshotParameter.findBySnapshot(dbOriginal.getSnapshotId());
		
		for (SnapshotParameter parameterOriginal : parameterOriginals) {
			SnapshotParameter parameterCopy = new SnapshotParameter();
			
			SnapshotParameterId parameterCopyId = new SnapshotParameterId();
			parameterCopyId.setSnapshotId(dbInstance.getSnapshotId());
			parameterCopyId.setParameterName(parameterOriginal.getId().getParameterName());
			parameterCopyId.setValueId(parameterOriginal.getId().getValueId());
			
			parameterCopy.setId(parameterCopyId);

			parameterCopy.setParameterSource(parameterOriginal.getParameterSource());
			parameterCopy.setParameterGroup(parameterOriginal.getParameterGroup());
			
			parameterCopy.setParameterType(parameterOriginal.getParameterType());
			
			parameterCopy.setDateFromValue(parameterOriginal.getDateFromValue());
			parameterCopy.setDateToValue(parameterOriginal.getDateToValue());
			parameterCopy.setDblFromValue(parameterOriginal.getDblFromValue());
			parameterCopy.setDblToValue(parameterOriginal.getDblToValue());
			parameterCopy.setIntFromValue(parameterOriginal.getIntFromValue());
			parameterCopy.setIntToValue(parameterOriginal.getIntToValue());
			parameterCopy.setStrFromValue(parameterOriginal.getStrFromValue());
			parameterCopy.setStrToValue(parameterOriginal.getStrToValue());
			
			DbSnapshotParameter.persist(parameterCopy);
		}
	}
	
	public void copySnapshotProductServices(Snapshot dbInstance, Snapshot dbOriginal) {
		List<SnapshotProductService> productServiceOriginals = DbSnapshotProductService.findFiltered(dbOriginal.getSnapshotId(), (char) 0, null);
		
		for (SnapshotProductService productServiceOriginal : productServiceOriginals) {
			SnapshotProductService productServiceCopy = new SnapshotProductService();
			
			SnapshotProductServiceId productServiceCopyId = new SnapshotProductServiceId();
			productServiceCopyId.setSnapshotId(dbInstance.getSnapshotId());
			productServiceCopyId.setProductServiceCode(productServiceOriginal.getId().getProductServiceCode());
			
			productServiceCopy.setId(productServiceCopyId);
			
			DbSnapshotProductService.persist(productServiceCopy);
		}
		
	}
}
