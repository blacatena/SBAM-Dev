package com.scholastic.sbam.shared.validation;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * Validator for any basic code field.
 * 
 * @author Bob Lacatena
 *
 */
public class ZipValidator implements Validator {
	private Field<?> countryField = null;
	
	public ZipValidator() {
		super();
	}
	
	public ZipValidator(Field<?> countryField) {
		super();
		this.countryField = countryField;
	}

	@Override
	public String validate(Field<?> field, String value) {
		return validate(value);
	}
	
	public String validate(String value) {
		if (value == null)
			return null;
		value = value.trim();
		if (value.length() == 0)
			return null;
		if (countryField == null 
		||  countryField.getRawValue() == null 
		||  countryField.getRawValue().length() == 0 
		||  countryField.getRawValue().equalsIgnoreCase("US")
		||  countryField.getRawValue().equalsIgnoreCase("USA"))
			return validateUsaZip(value);
		return null;
	}
	
	public String validateUsaZip(String value) {
		if (value == null || value.length() == 0)
			return null;
		
		String [] parts = value.trim().split("-");
		
		if (parts.length > 2)
			return "A zip code can have at most two parts, separated by a dash.";
		
		if (parts.length > 0) {
			if (parts [0].length() != 5)
				return "The main zip code length must be exactly 5 digits.";
			if (!AppConstants.isNumeric(parts [0]))
				return "A USA zip code may only contain digits.";
		}
		if (parts.length > 1) {
			if (parts [1].length() != 4)
				return "A zip-plus-four must be exactly 4 digits.";
			if (!AppConstants.isNumeric(parts [1]))
				return "A zip-plus-four must contain only digits.";
		}
		
		return null;
	}

}
