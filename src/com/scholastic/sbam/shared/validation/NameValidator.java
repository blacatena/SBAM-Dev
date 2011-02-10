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
	private int    minLen = 0;
	private int	   maxLen = 0;
	private String label;
	
	public NameValidator() {
		super();
	}
	
	public NameValidator(int maxLen) {
		this();
		this.maxLen = maxLen;
	}
	
	public NameValidator(int minLen, int maxLen) {
		this();
		this.minLen = minLen;
		this.maxLen = maxLen;
	}
	
	public NameValidator(String label) {
		this();
		this.label = label;
	}
	
	public NameValidator(String label, int maxLen) {
		this(label);
		this.maxLen = maxLen;
	}
	
	public NameValidator(String label, int minLen, int maxLen) {
		this(label);
		this.maxLen = maxLen;
		this.minLen = minLen;
	}
	
	@Override
	public String validate(Field<?> field, String value) {
		return validate(value);
	}
	
	public String validate(String value) {
		if (value == null || value.length() == 0) {
			if (label == null)
				return "A name is required.";
			else
				return "A " + label + " is required.";
		} else if (maxLen > 0 && value.length() > maxLen) {
			if (label == null)
				return "Cannot be longer than " + maxLen + " characters.";
			else
				return label + " cannot be longer than " + maxLen + " characters.";
		} else if (minLen > 0 && value.length() < minLen) {
			if (label == null)
				return "Cannot be shorter than " + minLen + " characters.";
			else
				return label + " cannot be shorter than " + minLen + " characters.";
		}
		return null;
	}

}
