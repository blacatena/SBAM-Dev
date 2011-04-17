package com.scholastic.sbam.client.uiobjects.fields;

import com.extjs.gxt.ui.client.widget.form.LabelField;

/**
 * A version of the label field without an original value (i.e. the original value always matches the current value).
 * @author Bob Lacatena
 *
 */
public class ConstantLabelField extends LabelField {
	@Override
	public void setValue(Object value) {
		super.setValue(value);
		super.setOriginalValue(getValue());
	}
	
	@Override
	public void setOriginalValue(Object value) {
		setValue(value);
	}
}
