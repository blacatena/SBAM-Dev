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
		if (model != null && model.getProperties() != null && model.getProperties().containsKey(keyField))
			if (model.get(keyField) == null)
				return "";
			else
				return model.get(keyField).toString();
		if (model != null)
			return model.hashCode() + "";
		return "";
	}
}
