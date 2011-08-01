package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class InstitutionStateInstance implements BeanModelTag, IsSerializable {

	private static BeanModelFactory beanModelfactory;

	protected String stateCode;
	protected String description;
	protected String countryCode;
	
	public String getStateCode() {
		return stateCode;
	}
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	public static InstitutionStateInstance getEmptyInstance() {
		InstitutionStateInstance instance = new InstitutionStateInstance();
		instance.stateCode = "";
		instance.description = "";
		return instance;
	}
	
	public static InstitutionStateInstance getUnknownInstance(String code) {
		InstitutionStateInstance instance = new InstitutionStateInstance();
		instance.stateCode = code;
		instance.description = "Unknown state " + code;
		return instance;
	}
	
	public static BeanModel obtainModel(InstitutionStateInstance instance) {
		if (beanModelfactory == null)
			beanModelfactory  = BeanModelLookup.get().getFactory(InstitutionStateInstance.class);
		BeanModel model = beanModelfactory.createModel(instance);
		return model;
	}

	public String toString() {
		return description;
	}
	
}
