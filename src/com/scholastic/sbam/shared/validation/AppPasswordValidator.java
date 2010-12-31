package com.scholastic.sbam.shared.validation;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;

/**
 * Validator for application (not client) password fields.
 * 
 * @author Bob Lacatena
 *
 */
public class AppPasswordValidator implements Validator {

	@Override
	public String validate(Field<?> field, String value) {
		return validate(value);
	}
	
	public String validate(String value) {
		if (value == null || value.length() < 6)
			return "A password must be at least six characters in length.";
		if (value != null && !value.matches("^.*[0-9].*$"))
			return "A password must contain at least one digit.";
		if (value != null && !value.toLowerCase().matches("^.*[a-z].*$"))
			return "A password must contain at least one letter.";
		return null;
	}

}
