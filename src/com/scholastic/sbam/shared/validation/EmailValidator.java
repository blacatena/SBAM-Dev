package com.scholastic.sbam.shared.validation;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;

/**
 * Validator for any email address field.
 * 
 * @author Bob Lacatena
 *
 */
public class EmailValidator implements Validator {

	@Override
	public String validate(Field<?> field, String value) {
		return validate(value);
	}
	
	public String validate(String value) {
		if (value == null || value.length() < 6)
			return "An email must be at least six characters in length.";
		if (!value.matches("^[a-zA-Z0-9_\\.]+@[a-zA-Z0-9_\\.]+\\.[a-zA-Z0-9_\\.]+$"))
			return "Invalid e-mail address.";
		return null;
	}

}
