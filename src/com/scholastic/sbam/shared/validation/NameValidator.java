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
	private String label;
	
	public NameValidator() {
		super();
	}
	
	public NameValidator(String label) {
		super();
		this.label = label;
	}
	
	@Override
	public String validate(Field<?> field, String value) {
		return validate(value);
	}
	
	public String validate(String value) {
		if (value == null || value.length() == 0)
			if (label == null)
				return "A name is required.";
			else
				return "A " + label + " is required.";
		return null;
	}

}
