package com.scholastic.sbam.shared.objects;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class ProductServiceTreeInstance implements BeanModelTag, IsSerializable {

	public static final String SERVICE = "service";
	public static final String FOLDER  = "folder";
	
	private String productCode;
	private String serviceCode;
	private String description;
	private String type;
	private List<ProductServiceTreeInstance> childInstances = new ArrayList<ProductServiceTreeInstance>();
	private boolean selected;
	
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
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
	public List<ProductServiceTreeInstance> getChildInstances() {
		return childInstances;
	}
	public void setChildInstances(List<ProductServiceTreeInstance> children) {
		if (children == null)
			children = new ArrayList<ProductServiceTreeInstance>();
		else
			this.childInstances = children;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public String toString() {
		return "[" + type + " / " + productCode + " / " + serviceCode + " / " + description + " / " + selected + " / <<<" + childInstances + ">>> ]"; 
	}
	
	public void addChildInstance(ProductServiceTreeInstance child) {
		if (childInstances == null)
			childInstances = new ArrayList<ProductServiceTreeInstance>();
		childInstances.add(child);
	}
}
