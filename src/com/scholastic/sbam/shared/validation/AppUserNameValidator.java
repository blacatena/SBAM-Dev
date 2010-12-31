package com.scholastic.sbam.shared.validation;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;

/**
 * Validator for application (not client) user name fields.
 * 
 * @author Bob Lacatena
 *
 */
public class AppUserNameValidator implements Validator {

	@Override
	public String validate(Field<?> field, String value) {
		if (value == null || value.length() < 6)
			return "A password must be at least six characters in length.";
		return null;
	}

}
