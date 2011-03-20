package com.scholastic.sbam.client.uiobjects;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.i18n.client.NumberFormat;

/**
 * A base class which provides useful helper methods for any portlet that will be using fields.
 * 
 * Classes include methods for creating grid columns and for constructing addresses in HTML.
 * 
 * @author Bob Lacatena
 *
 * @param <I>
 */
public abstract class FieldSupportContainer extends LayoutContainer {
	
	public FieldSupportContainer() {
		super();
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
		return FieldFactory.getDollarField(label);
	}
	
	protected NumberField getIntegerField(String label) {
		return FieldFactory.getIntegerField(label);
	}
	
	protected NumberField getNumberField(String label, NumberFormat numberFormat) {
		return FieldFactory.getNumberField(label, numberFormat);
	}
	
	protected TextField<String> getTextField(String label) {
		return FieldFactory.getTextField(label);
	}
	
	protected DateField getDateField(String label) {
		return FieldFactory.getDateField(label);
	}
	
	protected EnhancedComboBox<BeanModel> getComboField(String name, String label, int width, ListStore<BeanModel> listStore, String displayField) {
		return FieldFactory.getComboField(name, label, width, listStore, displayField);
	}
	
	protected EnhancedComboBox<BeanModel> getComboField(String name, String label, int width, String toolTip, ListStore<BeanModel> listStore, String displayField) {
		return FieldFactory.getComboField(name, label, width, toolTip, listStore, displayField);
	}
	
	protected EnhancedComboBox<BeanModel> getComboField(String name, String label, int width, String toolTip, ListStore<BeanModel> listStore, String valueField, String displayField) {
		return FieldFactory.getComboField(name, label, width, toolTip, listStore, valueField, displayField);
	}
	
	public abstract void awaken();
	
	public abstract void sleep();

}