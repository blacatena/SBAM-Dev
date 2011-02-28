package com.scholastic.sbam.shared.objects;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class HelpTextIndexInstance implements BeanModelTag, IsSerializable {
	private	String	id;
	private String	title;
	private String	iconName;
	private List<HelpTextIndexInstance> children = new ArrayList<HelpTextIndexInstance>();
	
	public void add(HelpTextIndexInstance child) {
		children.add(child);
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getIconName() {
		return iconName;
	}
	public void setIconName(String iconName) {
		this.iconName = iconName;
	}
	public List<HelpTextIndexInstance> getChildren() {
		return children;
	}
	public void setChildren(List<HelpTextIndexInstance> children) {
		this.children = children;
	}
	
	
}
