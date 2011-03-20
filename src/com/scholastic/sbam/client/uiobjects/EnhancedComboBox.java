package com.scholastic.sbam.client.uiobjects;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.form.ComboBox;

public class EnhancedComboBox<M extends ModelData> extends ComboBox<M> {
	private boolean typeAheadOne = true;
	
	@Override
	protected void onTypeAhead() {
		if (store.getCount() == 1)
			super.onTypeAhead();
	}

	public boolean isTypeAheadOne() {
		return typeAheadOne;
	}

	public void setTypeAheadOne(boolean typeAheadOne) {
		this.typeAheadOne = typeAheadOne;
	}
}
