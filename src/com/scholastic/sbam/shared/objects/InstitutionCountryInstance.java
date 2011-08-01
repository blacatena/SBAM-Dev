package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class InstitutionCountryInstance implements BeanModelTag, IsSerializable {

	private static BeanModelFactory beanModelfactory;

	private String countryCode;
	private String description;
	
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public static InstitutionCountryInstance getEmptyInstance() {
		InstitutionCountryInstance instance = new InstitutionCountryInstance();
		instance.countryCode = "";
		instance.description = "";
		return instance;
	}
	
	public static InstitutionCountryInstance getUnknownInstance(String code) {
		InstitutionCountryInstance instance = new InstitutionCountryInstance();
		instance.countryCode = code;
		instance.description = "Unknown country " + code;
		return instance;
	}

	public static BeanModel obtainModel(InstitutionCountryInstance instance) {
		if (beanModelfactory == null)
			beanModelfactory  = BeanModelLookup.get().getFactory(InstitutionCountryInstance.class);
		BeanModel model = beanModelfactory.createModel(instance);
		return model;
	}

	public String toString() {
		return description;
	}
	
}
