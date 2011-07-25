package com.scholastic.sbam.server.servlets;

import java.util.List;

import com.scholastic.sbam.client.services.SnapshotParameterSetGetService;
import com.scholastic.sbam.server.database.codegen.SnapshotParameter;
import com.scholastic.sbam.server.database.objects.DbSnapshotParameter;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.SnapshotParameterSetInstance;
import com.scholastic.sbam.shared.objects.SnapshotParameterValueObject;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class SnapshotParameterSetGetServiceImpl extends AuthenticatedServiceServlet implements SnapshotParameterSetGetService {

	@Override
	public SnapshotParameterSetInstance getSnapshotParameterSet(int snapshotId, String source) throws IllegalArgumentException {
		authenticate("get snapshot parameter set", SecurityManager.ROLE_QUERY);

		if (snapshotId <= 0)
			throw new IllegalArgumentException("A snapshot ID is required.");
//		Removed, so that null will return all parameters for a snapshot
//		if (source == null)
//			throw new IllegalArgumentException("A source is required.");
		
		SnapshotParameterSetInstance result = new SnapshotParameterSetInstance();
		
		result.setSnapshotId(snapshotId);
		result.setSource(source);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		try {
			List<SnapshotParameter> parameterValues = DbSnapshotParameter.findBySource(snapshotId, source);
			
			for (SnapshotParameter parameterValue: parameterValues) {
				if (parameterValue.getParameterType() == SnapshotParameterValueObject.INTEGER) {
					if (parameterValue.getIntFromValue() == null)
						throw new IllegalArgumentException("Null value found for " + parameterValue.getId().getParameterName() + " for snapshot " + snapshotId + ".");
					if (parameterValue.getIntToValue() != null && parameterValue.getIntToValue().intValue() > parameterValue.getIntFromValue().intValue())
						result.addValue(parameterValue.getId().getParameterName(), parameterValue.getParameterGroup(), parameterValue.getIntFromValue(), parameterValue.getIntToValue());
					else
						result.addValue(parameterValue.getId().getParameterName(), parameterValue.getParameterGroup(), parameterValue.getIntFromValue());
					
				} else if (parameterValue.getParameterType() == SnapshotParameterValueObject.DOUBLE) {
					if (parameterValue.getDblFromValue() == null)
						throw new IllegalArgumentException("Null value found for " + parameterValue.getId().getParameterName() + " for snapshot " + snapshotId + ".");
					if (parameterValue.getDblToValue().doubleValue() > parameterValue.getDblFromValue().doubleValue())
						result.addValue(parameterValue.getId().getParameterName(), parameterValue.getParameterGroup(), parameterValue.getDblFromValue(), parameterValue.getDblToValue());
					else
						result.addValue(parameterValue.getId().getParameterName(), parameterValue.getParameterGroup(), parameterValue.getDblFromValue());
					
				} else if (parameterValue.getParameterType() == SnapshotParameterValueObject.DATE) {
					if (parameterValue.getDateFromValue() == null)
						throw new IllegalArgumentException("Null value found for " + parameterValue.getId().getParameterName() + " for snapshot " + snapshotId + ".");
					if (parameterValue.getDateToValue() != null && parameterValue.getDateToValue().after(parameterValue.getDateFromValue()))
						result.addValue(parameterValue.getId().getParameterName(), parameterValue.getParameterGroup(), parameterValue.getDateFromValue(), parameterValue.getDateToValue());
					else
						result.addValue(parameterValue.getId().getParameterName(), parameterValue.getParameterGroup(), parameterValue.getDateFromValue());
					
				} else if (parameterValue.getParameterType() == SnapshotParameterValueObject.STRING) {
					if (parameterValue.getStrFromValue() == null)
						throw new IllegalArgumentException("Null value found for " + parameterValue.getId().getParameterName() + " for snapshot " + snapshotId + ".");
					if (parameterValue.getStrToValue() != null && parameterValue.getStrToValue().compareTo(parameterValue.getStrFromValue()) > 0)
						result.addValue(parameterValue.getId().getParameterName(), parameterValue.getParameterGroup(), parameterValue.getStrFromValue(), parameterValue.getStrToValue());
					else
						result.addValue(parameterValue.getId().getParameterName(), parameterValue.getParameterGroup(), parameterValue.getStrFromValue());
					
				} else if (parameterValue.getParameterType() == SnapshotParameterValueObject.BOOLEAN) {
					if (parameterValue.getIntFromValue() == null)
						throw new IllegalArgumentException("Null value found for " + parameterValue.getId().getParameterName() + " for snapshot " + snapshotId + ".");
					result.addValue(parameterValue.getId().getParameterName(), parameterValue.getParameterGroup(), parameterValue.getIntFromValue() > 0);
					
				} else 
					throw new IllegalArgumentException("Unrecognized Parameter Type " + parameterValue.getParameterType() + " for " + parameterValue.getId().getParameterName() + " for snapshot " + snapshotId + ".");
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
		} finally {		
			HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return result;
	}
}
