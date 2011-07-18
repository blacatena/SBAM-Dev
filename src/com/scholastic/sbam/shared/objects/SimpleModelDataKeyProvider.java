package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;

public class SimpleModelDataKeyProvider implements ModelKeyProvider<ModelData> {
	private final String keyField;
	
	public SimpleModelDataKeyProvider(String keyField) {
		this.keyField = keyField;
	}

	@Override
	public String getKey(ModelData model) {
		if (model.getProperties().containsKey(keyField))
			return model.get(keyField).toString();
		return model.hashCode() + "";
	}
}
