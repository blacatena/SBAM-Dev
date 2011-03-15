package com.scholastic.sbam.client.uiobjects;

import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.i18n.client.NumberFormat;
import com.scholastic.sbam.client.util.UiConstants;

/**
 * A base class which provides useful helper methods for any portlet that will be using fields.
 * 
 * Classes include methods for creating grid columns and for constructing addresses in HTML.
 * 
 * @author Bob Lacatena
 *
 * @param <I>
 */
public abstract class FieldSupportPortlet extends AppPortlet {
	
	public FieldSupportPortlet(String helpTextId) {
		super(helpTextId);
	}

	protected String plusIfNotEmpty(String value, String prefix) {
		if (value == null || value.length() == 0)
			return "";
		return prefix + value;
	}
	
	protected String brIfNotEmpty(String value) {
		return plusIfNotEmpty(value, "<br/>");
	}
	
	protected String commaIfNotEmpty(String value) {
		return plusIfNotEmpty(value, ", ");
	}
	
	protected String spaceIfNotEmpty(String value) {
		return plusIfNotEmpty(value, "&nbsp;&nbsp;&nbsp;");
	}
	
	protected String brIfNotUsa(String value) {
		if (value == null || value.length() == 0)
			return "";
		if (value.equalsIgnoreCase("USA"))
			return "";
		return "<br/>" + value;
	}
	
	protected NumberField getDollarField(String label) {
		return getNumberField(label, UiConstants.DOLLARS_FORMAT);
	}
	
	protected NumberField getIntegerField(String label) {
		return getNumberField(label, UiConstants.INTEGER_FORMAT);
	}
	
	protected NumberField getNumberField(String label, NumberFormat numberFormat) {
		NumberField field = new NumberField();
		setStandard(field, label);
		
		field.setFormat(numberFormat);
		field.setAllowDecimals(false);
		field.setAllowNegative(false);

//		field.setMessageTarget(messageTarget)
//		field.setMessages(messages);
//		field.setImages(images);
//		field.setReadOnly(true);
		
		return field;
	}
	
	protected TextField<String> getTextField(String label) {
		TextField<String> field = new TextField<String>();
		setStandard(field, label);
		return field;
	}
	
	protected DateField getDateField(String label) {
		DateField field = new DateField();
		setStandard(field, label);
		
		return field;
	}
	
	protected void setStandard(Field<?> field, String label) {
		field.setEnabled(false);
		field.addStyleName("field-or-label");
		field.setFieldLabel(label);
	}

	/**
	 * Go to sleep when collapsed.
	 */
	@Override
	public void onCollapse() {
		super.onCollapse();
		sleep();
	}
	
	/**
	 * Wake up when expanded.
	 */
	@Override
	public void onExpand() {
		super.onExpand();
		awaken();
	}
	
	public abstract void awaken();
	
	public abstract void sleep();

}