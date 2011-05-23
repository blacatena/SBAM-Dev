package com.scholastic.sbam.client.uiobjects.fields;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.MultiField;

/**
 * Extends MultiField to add the capabilities to automatically set original values, and to disable the component fields rather than the multifield as a whole.
 * 
 * Both characteristics can be turned on or off individually.
 * 
 * @author Bob Lacatena
 *
 * @param <D>
 */
public class EnhancedMultiField<D> extends MultiField<D> {
	/**	
	 * True if component fields should be disabled individually, rather than together
	 */
	protected boolean	disableFieldsIndividually	= true;
	/**
	 * 	True if the original value should be applied to the first field as well
	 */
	protected boolean	setFirstFieldValue			= true;
	/* TODO Expand this to work with an Object [] to set values, so that a call like setOriginalValue(getValue()) will work seamlessly. */
	
	public EnhancedMultiField() {
		super();
	}
	
	public EnhancedMultiField(String label) {
		super(label);
	}
	
	public void setOriginalValues() {
		for (Field<?> field : fields) {
			@SuppressWarnings("unchecked")
			Field<Object> dField = (Field<Object>) field;
			dField.setOriginalValue(dField.getValue());
		}
	}
	
	@Override
	public void setOriginalValue(D value) {
		super.setOriginalValue(value);
		if (fields != null && fields.size() > 0) {
			@SuppressWarnings("unchecked")
			Field<Object> field = (Field<Object>) fields.get(0);
			field.setOriginalValue(value);
		}
	}
	
	@Override
	public void onDisable() {
		if (disableFieldsIndividually) {
			for (Field<?> field : fields) {
				field.disable();
			}
		} else
			super.onDisable();
	}

	public boolean isDisableFieldsIndividually() {
		return disableFieldsIndividually;
	}

	public void setDisableFieldsIndividually(boolean disableFieldsIndividually) {
		this.disableFieldsIndividually = disableFieldsIndividually;
	}

	public boolean isSetFirstFieldValue() {
		return setFirstFieldValue;
	}

	public void setSetFirstFieldValue(boolean setFirstFieldValue) {
		this.setFirstFieldValue = setFirstFieldValue;
	}
	
}
