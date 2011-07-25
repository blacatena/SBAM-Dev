package com.scholastic.sbam.client.uiobjects.fields;

import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.Field;

/**
 * RadioGroup that properly returns its isDirty() value.
 * 
 * @author Bob Lacatena
 *
 */
public class EnhancedRadioGroup extends RadioGroup {
	@Override
	public boolean isDirty() {
		for (Field<?> field : getAll()) {
			if (field.isDirty())
				return true;
		}
		return false;
	}

}
