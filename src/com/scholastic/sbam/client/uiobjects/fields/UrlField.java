package com.scholastic.sbam.client.uiobjects.fields;

import com.extjs.gxt.ui.client.widget.form.TextField;

public class UrlField extends TextField<String> {

	@Override
	protected boolean validateValue(String value) {
		if (!getAllowBlank())
			if (value == null || value.trim().length() == 0) {
				markInvalid("A URL is required.");
				return false;
			}
		
		if (value == null || value.trim().length() == 0) {
			clearInvalid();
			return true;
		}
		
		if (value.startsWith("http://")) {
			clearInvalid();
			return true;
		}
		
		if (value.startsWith("https://")) {
			clearInvalid();
			return true;
		}
		
		if (value.startsWith("www.")) {
			clearInvalid();
			return true;
		}
		
		markInvalid("A URL must begein with http://, https:// or www.");
		return false;
	}
}
