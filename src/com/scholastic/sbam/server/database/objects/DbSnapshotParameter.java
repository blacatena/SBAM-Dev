package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.SnapshotParameter;
import com.scholastic.sbam.server.database.codegen.SnapshotParameterId;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.SnapshotParameterSetInstance;
import com.scholastic.sbam.shared.objects.SnapshotParameterValueObject;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbSnapshotParameter extends HibernateAccessor {
	
	static String objectName = SnapshotParameter.class.getSimpleName();
	
	public static SnapshotParameter getById(int snapshotId, String parameterName, int valueId) {
		SnapshotParameterId spsId = new SnapshotParameterId();
		spsId.setSnapshotId(snapshotId);
		spsId.setParameterName(parameterName);
		spsId.setValueId(valueId);
		
		return getById(spsId);
	}
	
	public static SnapshotParameter getById(SnapshotParameterId spsId) {
		SnapshotParameter snapshotParameter = (SnapshotParameter) sessionFactory.getCurrentSession().get(getObjectReference(objectName), spsId);
		return snapshotParameter;
	}
	
	public static List<SnapshotParameter> findAll() {
		List<Object> results = findAll(objectName);
		List<SnapshotParameter> reasons = new ArrayList<SnapshotParameter>();
		for (int i = 0; i < results.size(); i++)
			reasons.add((SnapshotParameter) results.get(i));
		return reasons;
	}
	
	public static List<SnapshotParameter> findBySnapshot(int snapshotId) {
		return findFiltered(snapshotId, null, null, null);
	}
	
	public static List<SnapshotParameter> findBySource(int snapshotId, String source) {
		return findFiltered(snapshotId, source, null, null);
	}
	
	public static List<SnapshotParameter> findByGroup(int snapshotId, String group) {
		return findFiltered(snapshotId, null, group, null);
	}
	
	public static List<SnapshotParameter> findByParameter(int snapshotId, String parameterName) {
		return findFiltered(snapshotId, null, null, parameterName);
	}
	
	public static List<SnapshotParameter> findFiltered(int snapshotId, String source, String group, String name) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (snapshotId > 0)
            	crit.add(Restrictions.like("id.snapshotId", snapshotId));
            if (source != null && source.length() > 0)
            	crit.add(Restrictions.like("parameterSource", source));
            if (group != null && group.length() > 0)
            	crit.add(Restrictions.like("parameterGroup", group));
            if (name != null && name.length() > 0)
            	crit.add(Restrictions.like("id.parameterName", name));
            crit.addOrder(Order.asc("id.snapshotId"));
            crit.addOrder(Order.asc("id.parameterName"));
            @SuppressWarnings("unchecked")
			List<SnapshotParameter> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<SnapshotParameter>();
	}

	public static int getNextValueId(int snapshotId, String parameterName) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
        crit.add(Restrictions.eq("id.snapshotId", snapshotId));
        crit.add(Restrictions.eq("id.parameterName", parameterName));
        crit.setMaxResults(1);
        crit.addOrder(Order.desc("id.valueId"));
        @SuppressWarnings("unchecked")
		List<SnapshotParameter> objects = crit.list();
        if (objects == null || objects.size() == 0)
        	return 1;
        return objects.get(0).getId().getValueId() + 1;
	}
	
	public static SnapshotParameterSetInstance getParameters(List<SnapshotParameter> parameterValues) {
		SnapshotParameterSetInstance result = new SnapshotParameterSetInstance();
		
		for (SnapshotParameter parameterValue: parameterValues) {
			if (parameterValue.getParameterType() == SnapshotParameterValueObject.INTEGER) {
				if (parameterValue.getIntFromValue() == null)
					throw new IllegalArgumentException("Null value found for " + parameterValue.getId().getParameterName() + " for snapshot " + parameterValue.getId().getSnapshotId() + ".");
				if (parameterValue.getIntToValue() != null && parameterValue.getIntToValue().intValue() > parameterValue.getIntFromValue().intValue())
					result.addValue(parameterValue.getId().getParameterName(), parameterValue.getParameterGroup(), parameterValue.getIntFromValue(), parameterValue.getIntToValue());
				else
					result.addValue(parameterValue.getId().getParameterName(), parameterValue.getParameterGroup(), parameterValue.getIntFromValue());
				
			} else if (parameterValue.getParameterType() == SnapshotParameterValueObject.DOUBLE) {
				if (parameterValue.getDblFromValue() == null)
					throw new IllegalArgumentException("Null value found for " + parameterValue.getId().getParameterName() + " for snapshot " + parameterValue.getId().getSnapshotId() + ".");
				if (parameterValue.getDblToValue().doubleValue() > parameterValue.getDblFromValue().doubleValue())
					result.addValue(parameterValue.getId().getParameterName(), parameterValue.getParameterGroup(), parameterValue.getDblFromValue(), parameterValue.getDblToValue());
				else
					result.addValue(parameterValue.getId().getParameterName(), parameterValue.getParameterGroup(), parameterValue.getDblFromValue());
				
			} else if (parameterValue.getParameterType() == SnapshotParameterValueObject.DATE) {
				if (parameterValue.getDateFromValue() == null)
					throw new IllegalArgumentException("Null value found for " + parameterValue.getId().getParameterName() + " for snapshot " + parameterValue.getId().getSnapshotId() + ".");
				if (parameterValue.getDateToValue() != null && parameterValue.getDateToValue().after(parameterValue.getDateFromValue()))
					result.addValue(parameterValue.getId().getParameterName(), parameterValue.getParameterGroup(), parameterValue.getDateFromValue(), parameterValue.getDateToValue());
				else
					result.addValue(parameterValue.getId().getParameterName(), parameterValue.getParameterGroup(), parameterValue.getDateFromValue());
				
			} else if (parameterValue.getParameterType() == SnapshotParameterValueObject.STRING) {
				if (parameterValue.getStrFromValue() == null)
					throw new IllegalArgumentException("Null value found for " + parameterValue.getId().getParameterName() + " for snapshot " + parameterValue.getId().getSnapshotId() + ".");
				if (parameterValue.getStrToValue() != null && parameterValue.getStrToValue().compareTo(parameterValue.getStrFromValue()) > 0)
					result.addValue(parameterValue.getId().getParameterName(), parameterValue.getParameterGroup(), parameterValue.getStrFromValue(), parameterValue.getStrToValue());
				else
					result.addValue(parameterValue.getId().getParameterName(), parameterValue.getParameterGroup(), parameterValue.getStrFromValue());
				
			} else if (parameterValue.getParameterType() == SnapshotParameterValueObject.BOOLEAN) {
				if (parameterValue.getIntFromValue() == null)
					throw new IllegalArgumentException("Null value found for " + parameterValue.getId().getParameterName() + " for snapshot " + parameterValue.getId().getSnapshotId() + ".");
				result.addValue(parameterValue.getId().getParameterName(), parameterValue.getParameterGroup(), parameterValue.getIntFromValue() > 0);
				
			} else 
				throw new IllegalArgumentException("Unrecognized Parameter Type " + parameterValue.getParameterType() + " for " + parameterValue.getId().getParameterName() + " for snapshot " + parameterValue.getId().getSnapshotId() + ".");
		}
		
		return result;
	}
}
