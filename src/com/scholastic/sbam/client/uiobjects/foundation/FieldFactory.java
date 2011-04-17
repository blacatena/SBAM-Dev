package com.scholastic.sbam.client.uiobjects.foundation;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.DateTimePropertyEditor;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.gwt.i18n.client.NumberFormat;
import com.scholastic.sbam.client.uiobjects.fields.BoundDateField;
import com.scholastic.sbam.client.uiobjects.fields.BoundSliderField;
import com.scholastic.sbam.client.uiobjects.fields.ConstantLabelField;
import com.scholastic.sbam.client.uiobjects.fields.EnhancedComboBox;
import com.scholastic.sbam.client.uiobjects.fields.InstitutionSearchField;
import com.scholastic.sbam.client.uiobjects.fields.SizedTextArea;
import com.scholastic.sbam.client.uiobjects.fields.SliderFieldWithDisable;
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
	
	protected static LabelField getLabelField() {
		return getLabelField(-1);
	}
	
	protected static LabelField getLabelField(int width) {
		LabelField field = new ConstantLabelField();
		setStandard(field, "");
		if (width > 0)
			field.setWidth(width);
		return field;
	}
	
	protected static NumberField getDollarField(String label) {
		return getNumberField(label, UiConstants.DOLLARS_FORMAT, -1);
	}
	
	protected static NumberField getDollarField(String label, int width) {
		return getNumberField(label, UiConstants.DOLLARS_FORMAT, width);
	}
	
	protected static NumberField getIntegerField(String label) {
		return getIntegerField(label, -1);
	}
	
	protected static NumberField getIntegerField(String label, int width) {
		return getNumberField(label, UiConstants.INTEGER_FORMAT, width);
	}
	
	protected static NumberField getNumberField(String label, NumberFormat numberFormat) {
		return getNumberField(label, numberFormat, -1);
	}
	
	protected static NumberField getNumberField(String label, NumberFormat numberFormat, int width) {
		NumberField field = new NumberField();
		setStandard(field, label);
		
		if (width >= 0)
			field.setWidth(width);
		field.setFormat(numberFormat);
//		field.setAllowDecimals(false);
		field.setAllowNegative(false);

//		field.setMessageTarget(messageTarget)
//		field.setMessages(messages);
//		field.setImages(images);
//		field.setReadOnly(true);
		
		return field;
	}
	
	protected static CheckBoxGroup getCheckBoxGroup(String label, CheckBox... boxes) {
		CheckBoxGroup cbGroup = new CheckBoxGroup();
		setStandard(cbGroup, label);
		for (CheckBox cb : boxes) {
			cbGroup.add(cb);
		}
		return cbGroup;
	}
	
	protected static CheckBox	getCheckBoxField(String label) {
		CheckBox checkBox = new CheckBox();
		setStandard(checkBox, null);
		checkBox.setBoxLabel(label);
		return checkBox;
	}
	
	protected static TextArea getMultiLineField(String label, int lines) {
		SizedTextArea field = new SizedTextArea();
		setStandard(field, label);
		field.setPreventScrollbars(true);
		if (lines > 0) {
			field.setRows(lines);
		}
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
	
	protected static BoundDateField getBoundDateField(String label) {
		BoundDateField field = new BoundDateField();
		setStandard(field, label);
		field.setPropertyEditor(new DateTimePropertyEditor(UiConstants.APP_DATE_LONG_FORMAT));
		
		return field;
	}
	
	protected static SliderFieldWithDisable getSliderField(String label) {
		Slider		slider = new Slider();
		SliderFieldWithDisable field = new SliderFieldWithDisable(slider);
		setStandard(field, label);
		
		return field;
	}
	
	protected static BoundSliderField getBoundSliderField(String label) {
		Slider		slider = new Slider();
		BoundSliderField field = new BoundSliderField(slider);
		setStandard(field, label);
	//	field.setPropertyEditor(new NumberPropertyEditor(UiConstants.INTEGER_FORMAT));
		
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
		combo.setTriggerStyle("trigger-square");
		if (width > 0)
			combo.setWidth(width);
			
		if (toolTip != null)
			combo.setToolTip(toolTip);
//		if (validator != null)
//			combo.setValidator(validator);
	//	combo.setEditable(false);
		
		combo.setStore(listStore);
		
		return combo;
	}

	protected static InstitutionSearchField getInstitutionSearchField(String name, String label) {
		return getInstitutionSearchField(name, label, 0, null, null, null);
	}

	protected static InstitutionSearchField getInstitutionSearchField(String name, String label, String toolTip) {
		return getInstitutionSearchField(name, label, 0, toolTip, null, null);
	}

	protected static InstitutionSearchField getInstitutionSearchField(String name, String label, int width, String toolTip) {
		return getInstitutionSearchField(name, label, width, toolTip, null, null);
	}
	
	protected static InstitutionSearchField getInstitutionSearchField(String name, String label, int width, String toolTip, String valueField, String displayField) {
		InstitutionSearchField instCombo = new InstitutionSearchField();
		setStandard(instCombo, label);
		
		if (toolTip != null)
			instCombo.setToolTip(toolTip);
		if (width > 0)
			instCombo.setWidth(width);
		if (valueField != null)
			instCombo.setValueField(valueField);
		if (displayField != null)
			instCombo.setDisplayField(displayField);
		
		return instCombo;
	}
	
	public static void setStandard(Field<?> field, String label) {
	//	field.setName(name);
		field.setEnabled(false);
		field.addStyleName("field-or-label");
	//	field.setLabelStyle("color: saddlebrown;");
		if (label == null)
			label = "";
		field.setFieldLabel(label);
		if (label == null || label.length() == 0)
			field.setLabelSeparator("");
	}

}