package com.scholastic.sbam.shared.validation;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * Validator for application (not client) user name fields.
 * 
 * @author Bob Lacatena
 *
 */
public class AppRoleGroupValidator implements Validator {

	@Override
	public String validate(Field<?> field, String value) {
		return validate(value);
	}
	
	public String validate(String value) {
		if (value == null || value.length() == 0)
			return "Invalid role.";
		for (int i = 0; i < SecurityManager.ROLE_GROUPS.length; i++) {
			if (value.equals(SecurityManager.ROLE_GROUPS [i].getGroupTitle()))
				return null;
		}
		return "Invalid role.";
	}

}
