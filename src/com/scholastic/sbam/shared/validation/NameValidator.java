package com.scholastic.sbam.shared.validation;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;

/**
 * Validator for any name field.
 * 
 * @author Bob Lacatena
 *
 */
public class NameValidator implements Validator {

	@Override
	public String validate(Field<?> field, String value) {
		return validate(value);
	}
	
	public String validate(String value) {
		if (value == null || value.length() == 0)
			return "A name is required.";
		return null;
	}

}
