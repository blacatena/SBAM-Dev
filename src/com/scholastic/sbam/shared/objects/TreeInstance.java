package com.scholastic.sbam.shared.objects;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public abstract class TreeInstance<T> implements BeanModelTag, IsSerializable {

	public static final String SERVICE = "service";
	public static final String FOLDER  = "folder";
	
	protected String description;
	protected String type;
	protected List<T> childInstances = new ArrayList<T>();
	protected boolean selected;
	
	public TreeInstance() {
		super();
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public List<T> getChildInstances() {
		return childInstances;
	}
	
	public void setChildInstances(List<T> children) {
		if (children == null)
			children = new ArrayList<T>();
		else
			this.childInstances = children;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public void addChildInstance(T child) {
		if (childInstances == null)
			childInstances = new ArrayList<T>();
		childInstances.add(child);
	}
	
	public void setAsFolder(String name) {
		setDescription(name);
		setType(FOLDER);
	}
	
	public abstract String getUniqueKey();
	
	public String toString() {
		return "[" + type + " / " + getUniqueKey() + " / " + description + " / " + selected + " / <<<" + childInstances + ">>> ]"; 
	}
}
