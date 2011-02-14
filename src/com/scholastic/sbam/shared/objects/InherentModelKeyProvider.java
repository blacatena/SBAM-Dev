package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;

public interface InherentModelKeyProvider {
	public ModelKeyProvider<BeanModel> obtainModelKeyProvider();
}
