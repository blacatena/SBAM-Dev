package com.scholastic.sbam.client.uiobjects;

import com.extjs.gxt.ui.client.widget.form.TriggerField;
import com.google.gwt.user.client.Element;

/**
 * A "field" which can be added to a form, like a field, and used as a button.
 * @author Bob Lacatena
 *
 */
public class IconButtonField<D> extends TriggerField<D> {
	
	@Override 
	protected void onRender(Element parent, int index) {
		setWidth(10);
		super.onRender(parent, index);
		
		//	Remove the input element from the screen -- if this doesn't work, then hide it, either here, or through CSS
		el().removeChild(input.dom);
	}
}
