package com.scholastic.sbam.shared.validation;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;

/**
 * Validator for any basic code field.
 * 
 * @author Bob Lacatena
 *
 */
public class CodeValidator implements Validator {
	private int minLen = 4;
	
	public CodeValidator() {
		super();
	}
	
	public CodeValidator(int minLen) {
		super();
		this.minLen = minLen;
	}

	@Override
	public String validate(Field<?> field, String value) {
		return validate(value);
	}
	
	public String validate(String value) {
		if (value == null || value.length() == 0)
			return "A code is required.";
		if (value.length() < minLen)
			return "A code must at least " + minLen + " characters in length.";
		return null;
	}

}
