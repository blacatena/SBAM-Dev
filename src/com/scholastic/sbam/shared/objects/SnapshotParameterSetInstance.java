package com.scholastic.sbam.shared.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class to carry a set of parameter data for a single source (card/view/form) for snapshots.
 * 
 * @author Bob Lacatena
 *
 */
public class SnapshotParameterSetInstance implements BeanModelTag, IsSerializable {
	
	private static BeanModelFactory beanModelfactory;

	protected int													snapshotId;
	protected String												source;
	protected HashMap<String, String>								groups	= new HashMap<String, String>();
	protected HashMap<String, List<SnapshotParameterValueObject>>	values	= new HashMap<String, List<SnapshotParameterValueObject>>();
	
	public SnapshotParameterSetInstance() {
		
	}
	
	public void clear() {
		groups.clear();
		values.clear();
	}
	
	public void clear(String parameterName) {
		groups.remove(parameterName);
		values.remove(parameterName);
	}
	
	public void clearValues(String parameterName) {
		values.remove(parameterName);
	}
	
	public void addValue(String parameterName, String group, Integer value) {
		if (value == null)
			return;
		groups.put(parameterName, group);
		addValue(parameterName, new SnapshotParameterValueObject(value));
	}
	
	public void addValue(String parameterName, String group, Date value) {
		if (value == null)
			return;
		groups.put(parameterName, group);
		addValue(parameterName, new SnapshotParameterValueObject(value));
	}
	
	public void addValue(String parameterName, String group, Double value) {
		if (value == null)
			return;
		groups.put(parameterName, group);
		addValue(parameterName, new SnapshotParameterValueObject(value));
	}
	
	public void addValue(String parameterName, String group, String value) {
		if (value == null)
			return;
		groups.put(parameterName, group);
		addValue(parameterName, new SnapshotParameterValueObject(value));
	}
	
	public void addValue(String parameterName, String group, Integer valueFrom, Integer valueTo) {
		if (valueFrom == null)
			return;
		groups.put(parameterName, group);
		addValue(parameterName, new SnapshotParameterValueObject(valueFrom, valueTo) );
	}
	
	public void addValue(String parameterName, String group, Date valueFrom, Date valueTo) {
		if (valueFrom == null)
			return;
		groups.put(parameterName, group);
		addValue(parameterName, new SnapshotParameterValueObject(valueFrom, valueTo) );
	}
	
	public void addValue(String parameterName, String group, Double valueFrom, Double valueTo) {
		if (valueFrom == null)
			return;
		groups.put(parameterName, group);
		addValue(parameterName, new SnapshotParameterValueObject(valueFrom, valueTo) );
	}
	
	public void addValue(String parameterName, String group, String valueFrom, String valueTo) {
		if (valueFrom == null)
			return;
		groups.put(parameterName, group);
		addValue(parameterName, new SnapshotParameterValueObject(valueFrom, valueTo) );
	}
	
	protected void addValue(String parameterName, SnapshotParameterValueObject value) {
		if (value == null)
			return;
		List<SnapshotParameterValueObject> valueList = null;
		if (values.containsKey(parameterName)) {
			valueList = values.get(parameterName);
		} else {
			valueList = new ArrayList<SnapshotParameterValueObject>();
			values.put(parameterName, valueList);
		}
		valueList.add(value);
	}
	
	public SnapshotParameterValueObject getValue(String name) {
		if (!values.containsKey(name))
			return null;
		
		List<SnapshotParameterValueObject> valueList = values.get(name);
		if (valueList.size() > 0)
			return valueList.get(0);
		
		return null;
	}
	
	public String getGroup(String name) {
		if (groups.containsKey(name))
			return groups.get(name);
		return "";
	}
	
	public List<SnapshotParameterValueObject> getValues(String name) {
		if (values.containsKey(name))
			return values.get(name);
		return null;
	}
	
	public int getSnapshotId() {
		return snapshotId;
	}

	public void setSnapshotId(int snapshotId) {
		this.snapshotId = snapshotId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public HashMap<String, String> getGroups() {
		return groups;
	}

	public void setGroups(HashMap<String, String> groups) {
		this.groups = groups;
	}

	public HashMap<String, List<SnapshotParameterValueObject>> getValues() {
		return values;
	}

	public void setValues(HashMap<String, List<SnapshotParameterValueObject>> values) {
		this.values = values;
	}

	public static BeanModel obtainModel(SnapshotParameterSetInstance instance) {
		if (beanModelfactory == null)
			beanModelfactory  = BeanModelLookup.get().getFactory(SnapshotParameterSetInstance.class);
		BeanModel model = beanModelfactory.createModel(instance);
		return model;
	}
	
	public String toString() {
		return source + " : " + values;
	}
}
