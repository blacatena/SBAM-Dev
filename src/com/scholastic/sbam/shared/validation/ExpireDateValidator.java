package com.scholastic.sbam.shared.validation;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;

/**
 * Validator for the expiration date field.
 * 
 * @author Bob Lacatena
 *
 */
public class ExpireDateValidator implements Validator {
	
	public ExpireDateValidator() {
		super();
	}
	
	@Override
	public String validate(Field<?> field, String value) {
		return validate(value);
	}
	
	public String validate(String value) {
		if (value == null || value.length() == 0) {
			return "An expiration date is required.";
		}
		return null;
	}

}
