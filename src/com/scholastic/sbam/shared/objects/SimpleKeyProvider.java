package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;

public class SimpleKeyProvider implements ModelKeyProvider<BeanModel> {
	private final String keyField;
	
	public SimpleKeyProvider(String keyField) {
		this.keyField = keyField;
	}

	@Override
	public String getKey(BeanModel model) {
		if (model.getProperties().containsKey(keyField))
			return model.get(keyField).toString();
		return model.hashCode() + "";
	}
}
