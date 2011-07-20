package com.scholastic.sbam.client.uiobjects.fields;

import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.Field;

/**
 * CheckBoxGroup the properly returns its isDirty() value.
 * 
 * @author Bob Lacatena
 *
 */
public class EnhancedCheckBoxGroup extends CheckBoxGroup {
	@Override
	public boolean isDirty() {
		for (Field<?> field : getAll()) {
			if (field.isDirty())
				return true;
		}
		return false;
	}

}
