package com.scholastic.sbam.shared.validation;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.Validator;

/**
 * Validator for any basic code field.
 * 
 * THIS CLASS IS NOT YET COMPLETED, and may not ever even be used/finished.
 * 
 * @author Bob Lacatena
 *
 */
public class SimpleComboValidator implements Validator {

	@Override
	public String validate(Field<?> field, String value) {
		if (field instanceof SimpleComboBox) {
			SimpleComboBox<?> combo = (SimpleComboBox<?>) field;
			System.out.println("Combo " + combo.getValue() + " from " + combo.getRawValue());
		} else 
			return "INTERNAL ERROR: Attempted to do a combo validate on a non SimpleComboBox field.";
		return null;
	}

}
