package com.scholastic.sbam.server.servlets;

import java.util.List;

import com.scholastic.sbam.client.services.UpdateSnapshotParameterSetService;
import com.scholastic.sbam.server.database.codegen.Snapshot;
import com.scholastic.sbam.server.database.codegen.SnapshotParameter;
import com.scholastic.sbam.server.database.codegen.SnapshotParameterId;
import com.scholastic.sbam.server.database.objects.DbSnapshot;
import com.scholastic.sbam.server.database.objects.DbSnapshotParameter;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.util.SnapshotMappings;
import com.scholastic.sbam.shared.objects.SnapshotInstance;
import com.scholastic.sbam.shared.objects.SnapshotParameterSetInstance;
import com.scholastic.sbam.shared.objects.SnapshotParameterValueObject;
import com.scholastic.sbam.shared.reporting.SnapshotParameterNames;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service to update just the name, status and/or note of a snapshot.
 */
@SuppressWarnings("serial")
public class UpdateSnapshotParameterSetServiceImpl extends AuthenticatedServiceServlet implements UpdateSnapshotParameterSetService {

	@Override
	public String updateSnapshotParameterSet(SnapshotParameterSetInstance parameterSet) throws IllegalArgumentException {
		
		if (parameterSet.getSnapshotId() <= 0)
			throw new IllegalArgumentException("Parameters can only be updated for an existing snapshot.");
		if (parameterSet.getSource() == null || parameterSet.getSource().trim().length() == 0)
			throw new IllegalArgumentException("A parameter source is required.");
		
		Snapshot dbSnapshot = null;
		
		authenticate("update snapshot parameter set", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Get existing
			dbSnapshot = DbSnapshot.getById(parameterSet.getSnapshotId());
			if (dbSnapshot == null)
				throw new IllegalArgumentException("Snapshot " + parameterSet.getSnapshotId() + " not found.");
				
			//	Remove all previous values for this source
			List<SnapshotParameter> parameters = DbSnapshotParameter.findBySource(parameterSet.getSnapshotId(), parameterSet.getSource());
			
			for (SnapshotParameter parameter : parameters) {
				DbSnapshotParameter.delete(parameter);
			}

			boolean updateSnapshot = false;
			for (String name : parameterSet.getValues().keySet()) {
				updateSnapshot = persistParameterValues(name, parameterSet, dbSnapshot);
			}
			
			if (updateSnapshot)
				DbSnapshot.persist(dbSnapshot);
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The snapshot parameter set update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return "";
	}
	
	protected boolean persistParameterValues(String name, SnapshotParameterSetInstance parameterSet, Snapshot snapshot) {
		int									snapshotId	= parameterSet.getSnapshotId();
		String								source		= parameterSet.getSource();
		String								group		= parameterSet.getGroup(name);
		List<SnapshotParameterValueObject>	values 		= parameterSet.getValues(name);
	
		for (SnapshotParameterValueObject value : values)
			if (value != null)
				persistParameterValue(snapshotId, source, name, group, value);
		
		//	Handle special updates to snapshot table
		if (SnapshotMappings.getParameterSqlMapping(name) == null) {
			if (name.equals(SnapshotParameterNames.UCN_TYPE)) {
				if (values.size() == 0 || values.get(0).getStringValue().length() == 0)
					snapshot.setUcnType(SnapshotInstance.BILL_UCN_TYPE);
				else
					snapshot.setUcnType(values.get(0).getStringValue().charAt(0));
			} else if (name.equals(SnapshotParameterNames.PRODUCT_SERVICE_TYPE)) {
				if (values.size() == 0 || values.get(0).getStringValue().length() == 0)
					snapshot.setProductServiceType(SnapshotInstance.SERVICE_TYPE);
				else
					snapshot.setProductServiceType(values.get(0).getStringValue().charAt(0));
			}
			return true;
		} else {
			return false;
		}
	}
	
	protected void persistParameterValue(int snapshotId, String source, String name, String group, SnapshotParameterValueObject value) {
		
		//	Just skip null values
		if (value == null)
			return;
		
		SnapshotParameter parameter = new SnapshotParameter();
		
		SnapshotParameterId parameterValueId = new SnapshotParameterId();
		parameterValueId.setSnapshotId(snapshotId);
		parameterValueId.setParameterName(name);
		parameterValueId.setValueId(DbSnapshotParameter.getNextValueId(snapshotId, name));
		
		parameter.setId(parameterValueId);
		
		parameter.setParameterGroup(group);
		parameter.setParameterSource(source);
		
		setParameterValue(parameter, value);
	
		DbSnapshotParameter.persist(parameter);
	}
	
	protected void setParameterValue(SnapshotParameter parameter, SnapshotParameterValueObject value) {
		parameter.setParameterType(value.getValueType());
		
		if (value.isString()) {
			parameter.setStrFromValue( value.getStringValue());
		} else if (value.isInteger()) {
			parameter.setIntFromValue( value.getIntValue());
		} else if (value.isDouble()) {
			parameter.setDblFromValue( value.getDoubleValue());
		} else if (value.isDate()) {
			parameter.setDateFromValue( value.getDateValue());
		} else if (value.isBoolean()) {
			parameter.setIntFromValue( value.getIntValue());
		}  else
			throw new IllegalArgumentException("Invalid value type " + value.getValueType() + " for parameter " + parameter.getId().getParameterName());
		
		if (value.isRange()) {
			if (value.getToValue().isString()) {
				parameter.setStrToValue( value.getToStringValue() );
			} else if (value.getToValue().isInteger()) {
				parameter.setIntToValue( value.getToIntValue() );
			} else if (value.getToValue().isDouble()) {
				parameter.setDblToValue( value.getToDoubleValue() );
			} else if (value.getToValue().isDate()) {
				parameter.setDateToValue( value.getToDateValue() );
			} else
				throw new IllegalArgumentException("Invalid range 'to' value type " + value.getToValue().getValueType() + " for parameter " + parameter.getId().getParameterName());
		}
	}
	
	private void silentRollback() {
		try {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.getSession().getTransaction().rollback();	
		} catch (Exception exc) { }
	}
}
