package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class ProductServiceTreeInstance implements BeanModelTag, IsSerializable {

	private String description;
	private String type;
	private ProductServiceTreeInstance [] children;
	
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
	public ProductServiceTreeInstance[] getChildren() {
		return children;
	}
	public void setChildren(ProductServiceTreeInstance[] children) {
		this.children = children;
	}
	
}
