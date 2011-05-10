package com.scholastic.sbam.client.uiobjects.foundation;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.google.gwt.i18n.client.NumberFormat;
import com.scholastic.sbam.client.uiobjects.fields.EnhancedComboBox;
import com.scholastic.sbam.client.uiobjects.uiapp.AppPortlet;
import com.scholastic.sbam.client.util.AddressFormatter;

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
	
	protected FormPanel getNewOuterFormPanel() {
		FormPanel newFormPanel = new FormPanel();
		
		newFormPanel.setFrame(false); // true
		newFormPanel.setHeaderVisible(false);  
		newFormPanel.setBodyBorder(false);	// true
		newFormPanel.setBorders(false);
		newFormPanel.setBodyStyleName("subtle-form");
		newFormPanel.setButtonAlign(HorizontalAlignment.CENTER);
		newFormPanel.setLabelAlign(LabelAlign.RIGHT);
		newFormPanel.setLabelWidth(75);
		
		return newFormPanel;
	}
	
	protected FormInnerPanel getNewInnerFormPanel() {
		return getNewInnerFormPanel(false);
	}
	
	protected FormInnerPanel getNewInnerFormPanel(boolean outerForm) {
		FormInnerPanel newFormPanel = new FormInnerPanel(outerForm);
		
		newFormPanel.setFrame(false); // true
		newFormPanel.setHeaderVisible(false);  
		newFormPanel.setBodyBorder(false);	// true
		newFormPanel.setBorders(false);
		newFormPanel.setBodyStyleName("subtle-form");
		newFormPanel.setButtonAlign(HorizontalAlignment.CENTER);
		newFormPanel.setLabelAlign(LabelAlign.RIGHT);
		newFormPanel.setLabelWidth(75);
		
		return newFormPanel;
	}

	protected String plusIfNotEmpty(String value, String prefix) {
		return AddressFormatter.plusIfNotEmpty(value, prefix);
	}
	
	protected String brIfNotEmpty(String value) {
		return AddressFormatter.brIfNotEmpty(value);
	}
	
	protected String commaIfNotEmpty(String value) {
		return AddressFormatter.commaIfNotEmpty(value);
	}
	
	protected String spaceIfNotEmpty(String value) {
		return AddressFormatter.spaceIfNotEmpty(value);
	}
	
	protected String brIfNotUsa(String value) {
		return AddressFormatter.brIfNotUsa(value);
	}
	
	protected NumberField getDollarField(String label) {
		return FieldFactory.getDollarField(label);
	}
	
	protected NumberField getDollarField(String label, int width) {
		return FieldFactory.getDollarField(label, width);
	}
	
	protected NumberField getIntegerField(String label) {
		return FieldFactory.getIntegerField(label);
	}
	
	protected NumberField getIntegerField(String label, int width) {
		return FieldFactory.getIntegerField(label, width);
	}
	
	protected NumberField getNumberField(String label, NumberFormat numberFormat) {
		return FieldFactory.getNumberField(label, numberFormat);
	}
	
	protected NumberField getNumberField(String label, NumberFormat numberFormat, int width) {
		return FieldFactory.getNumberField(label, numberFormat, width);
	}
	
	protected LabelField getLabelField(String label) {
		return FieldFactory.getLabelField(label, 0);
	}
	
	protected LabelField getLabelField(String label, int width) {
		return FieldFactory.getLabelField(label, 0);
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