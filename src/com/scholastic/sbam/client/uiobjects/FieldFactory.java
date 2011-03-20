package com.scholastic.sbam.client.uiobjects;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.DateTimePropertyEditor;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.gwt.i18n.client.NumberFormat;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.SimpleKeyProvider;

/**
 * A base class which provides useful helper methods for any portlet that will be using fields.
 * 
 * Classes include methods for creating grid columns and for constructing addresses in HTML.
 * 
 * @author Bob Lacatena
 *
 * @param <I>
 */
public class FieldFactory {

	protected static String plusIfNotEmpty(String value, String prefix) {
		if (value == null || value.length() == 0)
			return "";
		return prefix + value;
	}
	
	protected static String brIfNotEmpty(String value) {
		return plusIfNotEmpty(value, "<br/>");
	}
	
	protected static String commaIfNotEmpty(String value) {
		return plusIfNotEmpty(value, ", ");
	}
	
	protected static String spaceIfNotEmpty(String value) {
		return plusIfNotEmpty(value, "&nbsp;&nbsp;&nbsp;");
	}
	
	protected static String brIfNotUsa(String value) {
		if (value == null || value.length() == 0)
			return "";
		if (value.equalsIgnoreCase("USA"))
			return "";
		return "<br/>" + value;
	}
	
	protected static NumberField getDollarField(String label) {
		return getNumberField(label, UiConstants.DOLLARS_FORMAT);
	}
	
	protected static NumberField getIntegerField(String label) {
		return getNumberField(label, UiConstants.INTEGER_FORMAT);
	}
	
	protected static NumberField getNumberField(String label, NumberFormat numberFormat) {
		NumberField field = new NumberField();
		setStandard(field, label);
		
		field.setFormat(numberFormat);
//		field.setAllowDecimals(false);
		field.setAllowNegative(false);

//		field.setMessageTarget(messageTarget)
//		field.setMessages(messages);
//		field.setImages(images);
//		field.setReadOnly(true);
		
		return field;
	}
	
	protected static TextField<String> getTextField(String label) {
		TextField<String> field = new TextField<String>();
		setStandard(field, label);
		return field;
	}
	
	protected static DateField getDateField(String label) {
		DateField field = new DateField();
		setStandard(field, label);
		field.setPropertyEditor(new DateTimePropertyEditor(UiConstants.APP_DATE_LONG_FORMAT));
		
		return field;
	}
	
	protected static EnhancedComboBox<BeanModel> getComboField(String name, String label, int width, ListStore<BeanModel> listStore, String displayField) {
		return getComboField(name, label, width, null, listStore, name, displayField);
	}
	
	protected static EnhancedComboBox<BeanModel> getComboField(String name, String label, int width, String toolTip, ListStore<BeanModel> listStore, String displayField) {
		return getComboField(name, label, width, toolTip, listStore, name, displayField);
	}
	
	protected static EnhancedComboBox<BeanModel> getComboField(String name, String label, int width, String toolTip, ListStore<BeanModel> listStore, String valueField, String displayField) {
		if (listStore.getKeyProvider() == null)
			listStore.setKeyProvider(new SimpleKeyProvider(valueField));
		
		final EnhancedComboBox<BeanModel> combo = new EnhancedComboBox<BeanModel>();
		setStandard(combo, label);
		combo.setForceSelection(true);
		combo.disableTextSelection(false);
		combo.setTriggerAction(TriggerAction.ALL);
		combo.setDisplayField(displayField);
		combo.setValueField(valueField);
		combo.setTypeAhead(true);

		if (toolTip != null)
			combo.setToolTip(toolTip);
//		if (validator != null)
//			combo.setValidator(validator);
	//	combo.setEditable(false);
		
		combo.setStore(listStore);
		
		return combo;
	}
	
	protected static void setStandard(Field<?> field, String label) {
		field.setEnabled(false);
		field.addStyleName("field-or-label");
	//	field.setLabelStyle("color: saddlebrown;");
		field.setFieldLabel(label);
	}

}