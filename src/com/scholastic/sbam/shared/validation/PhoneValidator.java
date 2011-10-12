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
public class PhoneValidator implements Validator {
	private Field<?> countryField = null;
	
	public PhoneValidator() {
		super();
	}
	
	public PhoneValidator(Field<?> countryField) {
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
		if (countryField == null || countryField.getRawValue() == null || countryField.getRawValue().length() == 0)
			return validateUsaPhone(value);
		return null;
	}
	
	public String validateUsaPhone(String value) {
		if (value == null || value.length() == 0)
			return null;
		
		int [] lens = new int [5];
		int part	= 0;
		int len 	= 0;
		int totLen	= 0;
		int parts	= 0;
		int lastPart = 0;
		int firstParts = 0;
		
		//	Count lengths
		for (int idx = 0; idx <= value.length(); idx++) {
			if (idx < value.length() && AppConstants.isDigit(value.charAt(idx))) {
				len++;
			} else {
				if (part < 4) {
					lens [part] = len;
					totLen += len;
				} else
					lens [4] += len;
				
				parts++;
				firstParts += lastPart;
				lastPart = len;
				
				part++;
				len = 0;
			}
		}
		
		
		//	Test for valid combinations
		if (lens [0] == 1 && value.charAt(0) == '1' && lens [1] == 10)
			return null;
		if (lens [0] == 1 && value.charAt(0) == '1' && lens [1] == 3 && lens [2] == 7)
			return null;
		if (lens [0] == 1 && value.charAt(0) == '1' && lens [1] == 3 && lens [2] == 3 && lens [3] == 4)
			return null;
		if (lens [0] == 10 && value.charAt(0) != '1')
			return null;
		if (lens [0] == 11 && value.charAt(0) == '1')
			return null;
		if (lens [0] == 1 && value.charAt(0) != '1')
			return "Select a foreign country to use a non-USA phone number.";
		if (lens [0] == 3 && lens [1] == 7)
			return null;
		if (lens [0] == 3 && lens [1] == 3 && lens [2] == 4)
			return null;
		
		if (lens [0] != 3)
			return "A USA area code must be three digits.";
		if (lens [1] != 3)
			return "A USA prefix must be three digits.";
		if (lens [2] != 4)
			return "A USA phone number must be ten digits.  To include an extension, separate it from the main number.";
		
		return "Invalid USA phone number.";
	}

}
