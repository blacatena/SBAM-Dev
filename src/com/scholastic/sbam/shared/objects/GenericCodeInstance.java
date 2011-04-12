package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class GenericCodeInstance  implements BeanModelTag, IsSerializable {

	private static BeanModelFactory beanModelfactory;
	
	private String code;
	private String name;
	
	public GenericCodeInstance(String name) {
		this.name = name;
	}
	
	public GenericCodeInstance(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public static BeanModel obtainModel(GenericCodeInstance instance) {
		if (beanModelfactory == null)
			beanModelfactory  = BeanModelLookup.get().getFactory(GenericCodeInstance.class);
		BeanModel model = beanModelfactory.createModel(instance);
		return model;
	}
	
	
}
