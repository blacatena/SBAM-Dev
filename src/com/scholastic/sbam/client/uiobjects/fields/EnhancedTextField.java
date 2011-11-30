package com.scholastic.sbam.client.uiobjects.fields;

import com.extjs.gxt.ui.client.widget.form.TextField;

public class EnhancedTextField extends TextField<String> {
	/**
	 * Returns the typed value of the field.
	 * 
	 * @return the fields value
	 */
	@Override
	public String getValue() {
		if (!rendered) {
			return value;
		}
		String v = getRawValue();
		if (emptyText != null && v.equals(emptyText)) {
			return null;
		}
		if (v == null || v.equals("")) {
			return "";
		}
		try {
			return propertyEditor.convertStringValue(v);
		} catch (Exception e) {
			return "";
		}
	}
}
