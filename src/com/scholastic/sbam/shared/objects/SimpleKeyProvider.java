package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;

public class SimpleKeyProvider implements ModelKeyProvider<BeanModel> {
	private String keyField = "uniqueKey";
	
	public SimpleKeyProvider(String keyField) {
		this.keyField = keyField;
	}

	@Override
	public String getKey(BeanModel model) {
		if (model == null)
			return "";
		if (model.getProperties().containsKey(keyField) && model.get(keyField) != null) {
			return model.get(keyField).toString();
		}
		return model.hashCode() + "";
	}
}
