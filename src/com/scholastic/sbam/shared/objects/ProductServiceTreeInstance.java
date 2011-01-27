package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class ProductServiceTreeInstance implements BeanModelTag, IsSerializable {

	public static final String SERVICE = "service";
	public static final String FOLDER  = "folder";
	
	private String productCode;
	private String serviceCode;
	private String description;
	private String type;
	private ProductServiceTreeInstance [] children = new ProductServiceTreeInstance [0];
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
	public ProductServiceTreeInstance[] getChildren() {
		return children;
	}
	public void setChildren(ProductServiceTreeInstance[] children) {
		if (children == null)
			children = new ProductServiceTreeInstance [0];
		else
			this.children = children;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
}
